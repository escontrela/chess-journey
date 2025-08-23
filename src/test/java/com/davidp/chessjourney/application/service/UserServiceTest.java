package com.davidp.chessjourney.application.service;

import com.davidp.chessjourney.application.factories.ApplicationServiceFactory;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class UserServiceTest {

  @Test
  public void testUserServiceCreation() {
    // Test that factory creates UserService instances correctly
    UserService userService1 = ApplicationServiceFactory.createUserService();
    UserService userService2 = ApplicationServiceFactory.createUserService();
    
    Assertions.assertNotNull(userService1, "UserService should not be null");
    Assertions.assertNotNull(userService2, "UserService should not be null");
    
    // Since we're not using singleton anymore, instances should be different
    Assertions.assertNotSame(userService1, userService2, "Different UserService instances should be created each time");
  }

  @Test
  public void testGetActiveUserIdDelegatesToAppProperties() {
    // Test that getActiveUserId method exists and delegates properly
    UserService userService = ApplicationServiceFactory.createUserService();
    
    // This should not throw an exception and should delegate to AppProperties
    long userId = userService.getActiveUserId();
    
    // We can't assert a specific value since it depends on configuration,
    // but we can assert that the method returns without throwing exceptions
    Assertions.assertTrue(userId >= 0L, "Active user ID should be non-negative");
  }
}