package com.rocketseat.planner.exceptions;

public class InvalidOccursAtException extends RuntimeException{
  public InvalidOccursAtException(){
    super("Data para criação de atividade inválida! Insira uma data condizente com as datas da viagem.");
  }
}
