package com.rocketseat.planner.exceptions;

public class UserFoundException extends RuntimeException{
  public UserFoundException(){
    super("Este usuário ja existe");
  }
}
