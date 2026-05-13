/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.group3.utils;

/**
 * Custom Exception for Invalid ID or Entity related errors
 * @author THUAN
 */
public class IdInvalidException extends Exception {
    
    public IdInvalidException(String message) {
        super(message);
    }
    
}
