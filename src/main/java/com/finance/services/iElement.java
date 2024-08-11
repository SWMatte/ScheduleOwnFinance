package com.finance.services;

import com.finance.utils.ExceptionCustom;

public interface iElement <T>{

     void addElement(T element) throws ExceptionCustom;

     Double visualizeAvailable();

}
