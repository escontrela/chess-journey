package com.davidp.chessjourney.application.service;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class UserServiceTest {

  @Test
  public void testUserServiceSingleton() {
    // Test that getInstance returns the same instance
    UserServiceImpl instance1 = UserServiceImpl.getInstance();
    UserServiceImpl instance2 = UserServiceImpl.getInstance();
    
    Assertions.assertSame(instance1, instance2, "UserServiceImpl should return the same instance");
  }

  @Test
  public void testGetActiveUserIdDelegatesToAppProperties() {
    // Test that getActiveUserId method exists and delegates properly
    UserServiceImpl userService = UserServiceImpl.getInstance();
    
    // This should not throw an exception and should delegate to AppProperties
    long userId = userService.getActiveUserId();
    
    // We can't assert a specific value since it depends on configuration,
    // but we can assert that the method returns without throwing exceptions
    Assertions.assertTrue(userId >= 0L, "Active user ID should be non-negative");
  }
}