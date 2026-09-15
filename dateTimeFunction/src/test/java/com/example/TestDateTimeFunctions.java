package com.example;

import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;


@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("Date Time Function Assignment")
public class TestDateTimeFunctions {

	@Test
	@DisplayName("Positive Test case - Check whether function returns today's date successfully")
	public void checkTodayTest() {
	  
		 LocalDate expected = LocalDate.now();
	     LocalDate actual = DateTimeFunctions.getTodaysDate();  
	     Assertions.assertEquals(expected,actual);
		
	}
	
	@Test
	@DisplayName("Positive Test case - Check whether function returns later date successfully")
	public void checkLaterDateByDaysTest() {
	  
		 int laterdays = 10;
		 LocalDate today = LocalDate.now();
	     LocalDate actual = DateTimeFunctions.getLaterDatebyDays(today,laterdays);
	     Assertions.assertEquals(today.plusDays(laterdays),actual);
		
	}
	
	@Test
	@DisplayName("Positive Test case - Check whether function returns previous date successfully")
	public void checkPreviousDateByWeeksTest() {
	  
		 int prevweeks = 15;
		 LocalDate today = LocalDate.now();
	     LocalDate actual = DateTimeFunctions.getPreviousDatebyWeeks(today,prevweeks);
	     Assertions.assertEquals(today.minusWeeks(prevweeks),actual);
		
	}
	
	@Test
	@DisplayName("Positive Test case - Check whether function returns date difference accurately")
	public void checkDifferenceBetweenTwoDates() {
	  
		 LocalDate date1 = LocalDate.of(2002, 10, 3);
		 LocalDate date2 = LocalDate.of(2012, 10, 7);
		 LocalDate today = LocalDate.now();
	     String actual = DateTimeFunctions.getTimeDifference(date1, date2);
	     //System.out.println(actual);
	     Assertions.assertEquals("Years-10:Months-0:Days-4",actual);
		
	}
	
	
}
