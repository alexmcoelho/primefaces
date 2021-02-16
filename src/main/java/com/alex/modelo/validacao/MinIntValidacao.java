package com.alex.modelo.validacao;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.alex.exception.ValidaCNPJ;
import com.alex.exception.ValidaCPF;
import com.google.common.base.Strings;
 
public class MinIntValidacao implements ConstraintValidator<MinIntValid, Integer> {
 
	private int value;
	private int min;
    
    @Override
    public void initialize(MinIntValid constraintAnnotation) {
    	this.value = constraintAnnotation.value();
    	this.min = constraintAnnotation.min();
    }
 
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
    	if(value == null) {
            return true;
        }
 
        try {
        	if(value >= min) {
        		return true;
        	} 
        	throw new Exception();
        } catch (Exception nfex) {
            return false;
        }
    }
}
