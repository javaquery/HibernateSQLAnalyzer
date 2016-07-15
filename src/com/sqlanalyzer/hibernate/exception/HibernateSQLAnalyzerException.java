/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sqlanalyzer.hibernate.exception;

/**
 * @author vicky.thakor
 * @date 8th July, 2016
 */
public class HibernateSQLAnalyzerException extends RuntimeException{

    public HibernateSQLAnalyzerException(String message, Throwable cause) {
        super(message, cause);
    }
}
