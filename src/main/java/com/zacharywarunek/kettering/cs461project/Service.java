package com.zacharywarunek.kettering.cs461project;

import com.zacharywarunek.kettering.cs461project.entitys.*;
import com.zacharywarunek.kettering.cs461project.repositories.*;
import com.zacharywarunek.kettering.cs461project.util.AuthRequest;
import com.zacharywarunek.kettering.cs461project.service.CustomUserDetailsService;
import com.zacharywarunek.kettering.cs461project.config.JwtUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
public class Service {

    @Autowired
    IAccountRepo accountRepo;

    @Autowired
    ICategoryRepo categoryRepo;

    @Autowired
    IProductImagesRepo productImagesRepo;

    @Autowired
    IProductRepo productRepo;

    @Autowired
    IPaymentTypeRepo paymentTypeRepo;

    @Autowired
    IPaymentMethodRepo paymentMethodRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    public ResponseObject Register(String payloadFromUI){
        ResponseObject response = new ResponseObject();
        JSONObject jsonPayload = new JSONObject(payloadFromUI);
        jsonPayload.put("primeMember", false);
        Account account = new Account();
        try {

            account = account.constructEntity(jsonPayload.getString("firstName"), jsonPayload.getString("lastName"),hashPassword(jsonPayload.getString("password")),jsonPayload.getBoolean("primeMember"),jsonPayload.getString("email"));
            accountRepo.save(account);
            response.setStatus(200);
            response.setMessage("Account has been created");
            response.setData("Account has been created for " + account.getFirstName() + " " + account.getLastName());
        }
        catch (Exception e){
            response.setStatus(411);
            response.setMessage("An error occurred when creating your account");
            response.setData("An error occurred when creating an account :  " + e.getMessage());
        }
        return response;
    }
    public ResponseObject Authenticate(AuthRequest payloadFromUI){
        ResponseObject response = new ResponseObject();
        Account account;
        try {

            account = accountRepo.fetchAccountByEmail(payloadFromUI.getEmail());
            if(account != null && checkPassword(payloadFromUI.getPassword(), account.getPassword())){
                UserDetails userdetails = userDetailsService.loadUserByUsername(payloadFromUI.getEmail());
                account.setToken(jwtUtil.generateToken(userdetails));
                response.setStatus(200);
                response.setMessage("You are now logged in");
                response.setData(account);
            }
            else{
                response.setStatus(403);
                response.setMessage("Email or Password was incorrect");
            }
        }
        catch (Exception e){
            response.setStatus(411);
            response.setMessage("An error occurred when authenticating your account");
            response.setData("An error occurred when creating an account :  " + e.getMessage());
        }
        return response;
    }
    private String hashPassword(String password) {
        int workload = 12;
        String salt = BCrypt.gensalt(workload);
        String hashed_password = BCrypt.hashpw(password, salt);

        return(hashed_password);
    }
    private boolean checkPassword(String password_plaintext, String stored_hash) {
        boolean password_verified = false;

        if(null == stored_hash || !stored_hash.startsWith("$2a$"))
            throw new java.lang.IllegalArgumentException("Invalid hash provided for comparison");

        password_verified = BCrypt.checkpw(password_plaintext, stored_hash);

        return(password_verified);
    }

    public ResponseObject getProduct(int productId) {
        ResponseObject response = new ResponseObject();
        Product product = productRepo.fetchProductById(productId);
        if(product != null){
            response.setStatus(200);
            response.setMessage("Product was found by productId");
            response.setData(product);
        }
        else{
            response.setStatus(404);
            response.setMessage("Product was not found with that Product Id");
        }
        return response;

    }
    public ResponseObject getProductImages(int productId) {
        ResponseObject response = new ResponseObject();
        Collection<String> productImages = productImagesRepo.fetchProductImagesById(productId);
        if(productImages != null && !productImages.isEmpty()){
            response.setStatus(200);
            response.setMessage("Product Images were found by productId");
            response.setData(productImages);
        }
        else{
            response.setStatus(404);
            response.setMessage("Product Images were not found with that Product Id");
        }
        return response;

    }

    public ResponseObject getAllProducts() {
        ResponseObject response = new ResponseObject();
        Collection<Product> products = productRepo.fetchAllProducts();
        if(products != null){
            response.setStatus(200);
            response.setMessage("Products were found");
            response.setData(products);
        }
        else{
            response.setStatus(404);
            response.setMessage("No products were found");
        }
        return response;

    }

    public ResponseObject getAllCategories() {
        ResponseObject response = new ResponseObject();
        Collection<Category> categories = categoryRepo.fetchAllCategories();
        if(categories != null){
            response.setStatus(200);
            response.setMessage("Products were found");
            response.setData(categories);
        }
        else{
            response.setStatus(404);
            response.setMessage("No products were found");
        }
        return response;
    }

    public ResponseObject checkAccountExists(String email) {
        ResponseObject response = new ResponseObject();
        boolean exists = accountRepo.checkIfExists(email);
        response.setStatus(200);
        response.setData(exists);
        return response;

    }

    public ResponseObject changePrimeMembership(boolean member, int accountId) {
        ResponseObject response = new ResponseObject();
        accountRepo.changePrimeMembership(member, accountId);
        response.setStatus(200);
        response.setData(accountRepo.fetchAccountByAccountId(accountId));
        return response;
    }
    public ResponseObject changeAccountDetails(JSONObject jsonPayload){
        ResponseObject response = new ResponseObject();
        boolean updatePassword = jsonPayload.getBoolean("updatePassword");
        String password = updatePassword?hashPassword(jsonPayload.getString("password")): jsonPayload.getString("password");
        Account account = new Account();
        try {

            account = account.constructEntity(jsonPayload.getString("firstName"), jsonPayload.getString("lastName"),password,jsonPayload.getBoolean("primeMember"),jsonPayload.getString("email"));
            account.setAccountId(jsonPayload.getInt("accountId"));
            accountRepo.save(account);
            account.setToken(jsonPayload.getString("token"));
            response.setStatus(200);
            response.setMessage("Account has been updated");
            response.setData(account);
        }
        catch (Exception e){
            response.setStatus(411);
            response.setMessage("An error occurred when updating your account");
            response.setData("An error occurred when updating an account :  " + e.getMessage());
        }
        return response;
    }

    public ResponseObject checkPassword(AuthRequest payloadFromUI) {
        ResponseObject response = new ResponseObject();
        Account account;
        try {

            account = accountRepo.fetchAccountByEmail(payloadFromUI.getEmail());
            if(account != null && checkPassword(payloadFromUI.getPassword(), account.getPassword())){
                response.setStatus(200);
            }
            else{
                response.setStatus(403);
                response.setMessage("Current password was incorrect");
            }
        }
        catch (Exception e){
            response.setStatus(411);
            response.setMessage("An error occurred when authenticating your password");
        }
        return response;
    }


    public ResponseObject savePaymentMethod(JSONObject json){
        ResponseObject response = new ResponseObject();
        PaymentMethod paymentMethod = new PaymentMethod();
        try {
            paymentMethod = paymentMethod.constructEntity(json.getInt("accountId"), json.getInt("typeId"), json.getString("nameOnCard"), json.getString("cardNumber"), json.getString("exp"), json.getString("cvv"));
            if(json.has("pmid"))
                paymentMethod.setPmId(json.getInt("pmid"));

            paymentMethodRepo.save(paymentMethod);
            response.setStatus(200);
            response.setMessage("Payment Method has been created");
        }
        catch (Exception e){
            response.setStatus(411);
            response.setMessage("An error occurred when creating payment method");

        }
        return response;
    }

    public ResponseObject getAllPaymentMethods(int accountId) {
        ResponseObject response = new ResponseObject();
        String query = "SELECT pt.imageSrc, pt.TypeName, pm.NameOnCard, pm.CardNumber, pm.Cvv, pm.Exp, pm.Favorite, pm.PMID " +
                "FROM PaymentType pt " +
                "INNER JOIN PaymentMethod PM on pt.TypeId = PM.TypeId " +
                "where AccountID = " + accountId +
                " order by pm.Favorite DESC";
        Collection<JSONObject> paymentMethods = jdbcTemplate.query(query, new RowMapper<JSONObject>() {
            @Override
            public JSONObject mapRow(ResultSet rs, int i) throws SQLException {
                JSONObject json = new JSONObject();
                json.put("image", rs.getString(1));
                json.put("type", rs.getString(2));
                json.put("nameOnCard", rs.getString(3));
                json.put("cardNumber", rs.getString(4));
                json.put("cvv", rs.getString(5));
                json.put("exp", rs.getString(6));
                json.put("favorite", rs.getBoolean(7));
                json.put("PMID", rs.getInt(8));
                return json;
            }
        });
        if(!paymentMethods.isEmpty()){
            response.setStatus(200);
            response.setMessage("Payment methods were found");
            response.setData(paymentMethods.toString());
        }
        else{
            response.setStatus(404);
            response.setMessage("No payment methods found");
        }
        return response;

    }

    public ResponseObject setPaymentMethodFavorite(JSONObject jsonPayload) {
        ResponseObject response = new ResponseObject();
        String query = "update PaymentMethod set Favorite = 0 where AccountID = "+jsonPayload.getInt("accountId")+" and Favorite = 1;" +
                " update PaymentMethod set Favorite = 1 where AccountID = "+jsonPayload.getInt("accountId")+" and PMID =  " + jsonPayload.getInt("PMID");
        jdbcTemplate.update(query);
        response.setStatus(200);
        return response;
    }

    public ResponseObject getAllPaymentTypes() {
        ResponseObject response = new ResponseObject();
        Collection<PaymentType> paymentTypes = paymentTypeRepo.fetchAllPaymentTypes();
        response.setStatus(200);
        response.setData(paymentTypes);
        return response;

    }
    public ResponseObject deletePaymentMethod(JSONObject json) {
        ResponseObject response = new ResponseObject();
        paymentMethodRepo.deleteById(json.getInt("pmid"));
        response.setStatus(200);
        return response;

    }
    public ResponseObject searchProducts(String k, int c, boolean prime){
        ResponseObject response = new ResponseObject();
        String[] list = k.split(" ");
        StringBuilder query = new StringBuilder();
        query.append("SELECT *\n" +
                "FROM(\n" +
                "        SELECT\n" +
                "            t1.PID,\n" +
                "            Name,\n" +
                "            Description,\n" +
                "            Seller,\n" +
                "            Price,\n" +
                "            PrimeEligible,\n" +
                "            Stock,\n" +
                "            Category,\n" +
                "            Image,\n" +
                "\n" +
                "            ROW_NUMBER() OVER(PARTITION BY t1.PID ORDER BY t2.PIID) AS Row\n" +
                "        FROM Product t1\n" +
                "                 LEFT JOIN ProductImages t2\n" +
                "                           ON t1.PID = t2.PID) AS X where Row = 1 and");
        for(String word: list){
            query.append(" (Name like '%").append(word).append("%' or Description like '%").append(word).append("%' ").append("or");
        }
        query.replace(query.length() - 2, query.length(), ")");
        if(c!=0){
            query.append(" and Category=").append(c);
        }
        if(prime){
            query.append(" and PrimeEligible=").append(1);
        }
        query.append(";");
        Collection<JSONObject> products = jdbcTemplate.query(query.toString(), new RowMapper<JSONObject>() {
            @Override
            public JSONObject mapRow(ResultSet rs, int i) throws SQLException {
                JSONObject products = new JSONObject();
                products.put("productId", rs.getInt(1));
                products.put("name", rs.getString(2));
                products.put("description", rs.getString(3));
                products.put("seller", rs.getString(4));
                products.put("price", rs.getDouble(5));
                products.put("primeEligible", rs.getBoolean(6));
                products.put("stock", rs.getInt(7));
                products.put("category", rs.getInt(8));
                products.put("image", rs.getString(9));
                return products;
            }
        });
        response.setData(products.toString());
        return response;
    }

    public ResponseObject saveProduct(JSONObject payload) {
        ResponseObject response = new ResponseObject();
        Product product = new Product();
        product = product.constructEntity(payload.getString("name"),
                payload.getString("description"),
                payload.getString("seller"),
                payload.getDouble("price"),
                payload.getBoolean("primeEligible"),
                payload.getInt("stock"),
                payload.getInt("category"));
        product = productRepo.save(product);
        String[] imageList = payload.getString("image").split(",");
        for(String image: imageList){
            ProductImages productImages = new ProductImages();
            productImages = productImages.constructEntity(product.getProductId(),
                    image);
            productImagesRepo.save(productImages);

        }
        response.setStatus(200);
        return response;
    }
}
