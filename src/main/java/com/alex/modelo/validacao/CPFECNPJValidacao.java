package com.alex.modelo.validacao;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.alex.exception.ValidaCNPJ;
import com.alex.exception.ValidaCPF;
import com.google.common.base.Strings;
 
public class CPFECNPJValidacao implements ConstraintValidator<CPFECNPJValid, String> {
 
	private String value;
    
    @Override
    public void initialize(CPFECNPJValid constraintAnnotation) {
        this.value = constraintAnnotation.value();
    }
 
    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
    	if(Strings.isNullOrEmpty(value)) {
            return true;
        }
 
    	if(ValidaCPF.tiraPontoeTraco(value).equals("00000000000") 
    			|| ValidaCNPJ.tiraPontoeTraco(value).equals("00000000000000")) {
    		return true;
    	}
    	
        try {
        	if(ValidaCPF.isCPF(ValidaCPF.tiraPontoeTraco(value))
    				|| ValidaCNPJ.isCNPJ(ValidaCNPJ.tiraPontoeTraco(value))) {
        		return true;
        	} 
        	throw new Exception();
        } catch (Exception nfex) {
            return false;
        }
    }
}
