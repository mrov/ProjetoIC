package br.mafia.server.webserver;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class testemap {
	public static void main(String[] args) {
		Map<String, String> mapaNomes = new HashMap<String, String>(); 
        mapaNomes.put("casa", "João Delfino");
        mapaNomes.put("carro", "Maria do Carmo");
        mapaNomes.put("lol", "Claudinei Silva");

        System.out.println(mapaNomes.get("casa"));
        String t = "ó";
        try {
			System.out.println(t.getBytes("UTF-8")[0]);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
