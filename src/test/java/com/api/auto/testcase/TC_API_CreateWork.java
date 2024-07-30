package com.api.auto.testcase;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;
import java.util.Map;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import com.api.auto.utils.PropertiesFileUtils;

public class TC_API_CreateWork {
	
	private Response response;
	private ResponseBody responseBody;
	private JsonPath jsonBody;
	
	private String myWork =  PropertiesFileUtils.getProperty("nameWork");
	private String myExperience = PropertiesFileUtils.getProperty("experience");
	private String myEducation = PropertiesFileUtils.getProperty("education");
	
	@BeforeClass
	public void init() throws IOException {
		// Init data
		String baseUrl = PropertiesFileUtils.getProperty("baseurl");
		String createWorkPath = PropertiesFileUtils.getProperty("workPath");
		//String token = PropertiesFileUtils.getToken("token");
		
		FileInputStream file = new FileInputStream("./data/asm3.xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheet("Sheet1");
        XSSFRow row = sheet.getRow(1);
        XSSFCell cell = row.getCell(1);
		String token = cell.getStringCellValue();
		
		
		RestAssured.baseURI = baseUrl;
		// make body
		Map<String, Object> body = new HashMap<String, Object>();
             body.put("nameWork", myWork);
             body.put("experience", myExperience);
             body.put("education", myEducation);

             RequestSpecification request = RestAssured.given()
				.contentType(ContentType.JSON)
				.header("token", token)
				.body(body);

		response = request.post(createWorkPath);
		responseBody = response.body();
		jsonBody = responseBody.jsonPath();

		System.out.println(" " + responseBody.asPrettyString());
	}
	
	@Test(priority = 0)
	public void TC01_Validate201Created() {
              // kiểm chứng status code
               assertEquals(201, response.getStatusCode(), "Status Check Failed!");
	}

	@Test(priority = 1)
	public void TC02_ValidateWorkId() {
              // kiểm chứng id
        assertTrue(responseBody.asString().contains("id"), "Id check Failed!");
	}

	@Test(priority = 2)
	public void TC03_ValidateNameOfWorkMatched() {
              // kiểm chứng tên công việc nhận được có giống lúc tạo
        assertTrue(responseBody.asString().contains("nameWork"), "Work name field check Failed!");
        String n = jsonBody.getString("nameWork");
        assertEquals(myWork, n, "Work name not match!");
	}

	@Test(priority = 3)
	public void TC03_ValidateExperienceMatched() {
              // kiểm chứng kinh nghiệm nhận được có giống lúc tạo
        assertTrue(responseBody.asString().contains("experience"), "Experience field check Failed!");
        String ex = jsonBody.getString("experience");
        assertEquals(myExperience, ex, "Experience not match!");
	}

	@Test(priority = 4)
	public void TC03_ValidateEducationMatched() {
              // kiểm chứng học vấn nhận được có giống lúc tạo
        assertTrue(responseBody.asString().contains("education"), "Education field check Failed!");
        String ed = jsonBody.getString("education");
        assertEquals(myEducation, ed, "Education not match!");
	}
	
    @AfterClass
    public void afterTest() {
        //kết thúc test hiện tại thì clear enpoint để chạy tiếp test liền sau trong test Suite
        // ( có ý nghĩa nếu test sau không sử dụng chung enpoint với test hiện tại)
        RestAssured.baseURI = null; //Setup Base URI
        RestAssured.basePath = null;

    }

}
