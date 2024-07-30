package com.api.auto.testcase;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.api.auto.utils.PropertiesFileUtils;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;

public class TC_API_Login {
	private String account;
	private String password;
	private Response response;
	private ResponseBody responseBody;
	private JsonPath jsonBody;

	@BeforeClass
	public void init() {
		// Init data
		String baseUrl = PropertiesFileUtils.getProperty("baseurl");
		String loginPath = PropertiesFileUtils.getProperty("loginpath");
		account = PropertiesFileUtils.getProperty("account");
		password = PropertiesFileUtils.getProperty("password");

		RestAssured.baseURI = baseUrl;
//		RestAssured.basePath = loginPath;
		// make body
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("account", account);
		body.put("password", password);

        RequestSpecification request = RestAssured.given()
                                 .contentType(ContentType.JSON)
                                 .body(body);

		response = request.post(loginPath);
		responseBody = response.body();
		jsonBody = responseBody.jsonPath();

		System.out.println(" " + responseBody.asPrettyString());
	}

	@Test(priority = 0)
	public void TC01_Validate200Ok() {        //kiểm chứng status code
        assertEquals(200, response.getStatusCode(), "Status Check Failed!");
	}

	@Test(priority = 1)
	public void TC02_ValidateMessage() {
              // kiểm chứng response body có chứa trường message hay không
              // kiểm chứng trường message có = "Đăng nhập thành công"
		assertTrue(responseBody.asString().contains("message"), "Message check Failed!");
        String msg = jsonBody.getString("message");
        if (null == msg) msg = "";
        assertEquals("Đăng nhập thành công", msg, "Message text field check Failed!");
	}

	@Test(priority = 2)
	public void TC03_ValidateToken() throws IOException {
           // kiểm chứng response body có chứa trường token hay không
		String token = jsonBody.getString("token");
        assertTrue(responseBody.asString().contains("token"), "Token check Failed!");
          // lưu lại token
      //  PropertiesFileUtils.saveToken(token, token);
		FileInputStream fis = new FileInputStream("./data/asm3.xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        XSSFSheet sheet = workbook.getSheet("Sheet1");
        Row titleRow = sheet.createRow(0);
        Cell cell = titleRow.createCell(0);
        cell.setCellValue("token");
        Row localRow = sheet.createRow(1);
        Cell localCell = localRow.createCell(1);
        localCell.setCellValue(token);
		FileOutputStream fos = new FileOutputStream("./data/asm3.xlsx");
		workbook.write(fos);
	}

	@Test(priority = 3)
	public void TC05_ValidateUserType() {
         // kiểm chứng response body có chứa thông tin user và trường type hay không
         // kiểm chứng trường type có phải là “UNGVIEN”
        assertTrue(responseBody.asString().contains("name"), "Name field check Failed!");
        assertTrue(responseBody.asString().contains("type"), "Type field check Failed!");
        String t = jsonBody.getString("user.type");
        assertEquals("UNGVIEN", t, "Type not match!");
	}

	@Test(priority = 4)
	public void TC06_ValidateAccount() {
          // kiểm chứng response chứa thông tin user và trường account hay không
          // Kiểm chứng trường account có khớp với account đăng nhập
          // kiểm chứng response chứa thông tin user và trường password hay không
          // Kiểm chứng trường password có khớp với password đăng nhập
        assertTrue(responseBody.asString().contains("user"), "User field check Failed!");
        assertTrue(responseBody.asString().contains("account"), "Accout field check Failed!");
        assertTrue(responseBody.asString().contains("password"), "Password field check Failed!");
        String a = jsonBody.getString("user.account");
        assertEquals(account, a, "Account not match!");
        String p = jsonBody.getString("user.password");
        assertEquals(password, p, "Password not match!");
	}
//    @AfterClass
//    public void afterTest() {
//        //kết thúc test hiện tại thì clear enpoint để chạy tiếp test liền sau trong test Suite
//        // ( có ý nghĩa nếu test sau không sử dụng chung enpoint với test hiện tại)
//        RestAssured.baseURI = null; //Setup Base URI
//        RestAssured.basePath = null;
//
//    }
}
