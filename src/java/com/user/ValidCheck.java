package com.user;

public class ValidCheck {
    public static boolean email(String email) {
        String[] parts = email.split("@");
        if(parts.length == 2 && !parts[0].isEmpty() && !parts[1].isEmpty()) {
            String[] part = parts[1].split("\\.");
            return part.length == 2 && !part[0].isEmpty() && !part[1].isEmpty();
        } else {
            return false;
        }
    }
    public static boolean contact(String contact) {
        return (contact.charAt(0) == '6' || contact.charAt(0) == '7' ||contact.charAt(0) == '8' ||contact.charAt(0) == '9') && (contact.length() == 10);
    }

    public static boolean password(String password) {
        int countDigit=0,countSpecial=0,countLowercase=0,countUppercase=0;
        for(int i=0;i<password.length();i++) {
            if(password.charAt(i)>='0' && password.charAt(i)<='9') {
                countDigit++;
            } else if (password.charAt(i)>='a' && password.charAt(i)<='z') {
                countLowercase++;
            } else if (password.charAt(i)>='A' && password.charAt(i)<='Z') {
                countUppercase++;
            } else {
                countSpecial++;
            }
        }
        return (password.length()>=8 && countDigit>=1 && countLowercase>=1 && countUppercase>=1 && countSpecial >=1);
    }
}
