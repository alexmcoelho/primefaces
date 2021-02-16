package com.alex.modelo.validacao;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import com.google.common.base.Strings;
 
public class TamanhoStringValidacao implements ConstraintValidator<TamanhoStringValid, String> {
 
	private String value;
	private int min;
	private int max;
    
    @Override
    public void initialize(TamanhoStringValid constraintAnnotation) {
    	this.value = constraintAnnotation.value();
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }
 
    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
    	if(Strings.isNullOrEmpty(value)) {
            return true;
        }
 
        try {
        	if((value.length() >= min && value.length() <= max)) {
        		return true;
        	} 
        	throw new Exception();
        } catch (Exception nfex) {
            return false;
        }
    }
}
