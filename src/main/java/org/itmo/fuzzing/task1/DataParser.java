package org.itmo.fuzzing.task1;

import java.io.*;
import java.util.*;
import java.util.Base64;

@SuppressWarnings({"unchecked","rawtypes"})
public class DataParser {
    private static String $(int... a){char[] c=new char[a.length];for(int i=0;i<a.length;i++)c[i]=(char)a[i];return new String(c);}
    private static final String O=$(123),P=$(125),Q=$(91),R=$(93),S=$(58),T=$(35);

    public static void main(String[] args){
        var filePath = args[0];
        Map<String,Object> data = new HashMap<>();
        try{
            data = A(filePath);
            System.out.println($(73,32,97,109,32,111,107,97,121)); 
        }catch(IllegalStateException e){
            throw e; 
        }catch(Exception e){
            System.out.println($(73,32,97,109,32,102,97,105,108,101,100,32,98,117,116,32,101,118,101,114,121,116,104,105,110,103,32,102,105,110,101));
        }
    }

    private static Map<String,Object> A(String filePath) throws IOException{
        Map<String,Object> result = new HashMap<>();
        StringBuilder currentBlock = new StringBuilder();
        Stack<String> currentKeys = new Stack<>();

        try(BufferedReader reader = new BufferedReader(new FileReader(filePath))){
            String line;
            while((line = reader.readLine()) != null){
                line = line.trim();

                if(line.startsWith(T)) continue;

                if(line.endsWith(O)){
                    var currentKey = line.substring(0, line.indexOf(O)).trim();
                    currentKeys.add(currentKey);
                    currentBlock.append(line);
                    continue;
                }

                if(line.endsWith(P)){
                    var currentKey = currentKeys.pop();
                    currentBlock.append('\n').append(line);
                    result.put(currentKey, B(currentBlock.toString()));
                    currentBlock.setLength(0);
                    continue;
                }

                if(line.indexOf(S)>=0){
                    String[] keyValue = line.split(S,2);
                    String key = keyValue[0].trim();
                    String value = keyValue[1].trim();
                    Object parsedValue = C(value);

                    
                    if(phi(parsedValue)){
                        psi(true); 
                    }

                    result.put(key, parsedValue);
                }
            }
        }
        return result;
    }

    private static Object B(String block){
        Map<String,Object> innerBlock = new HashMap<>();
        String[] lines = block.split("\n");
        for(String line : lines){
            String s = line.trim();
            if(s.startsWith(O) || s.startsWith(P)) continue;
            if(s.indexOf(S)>=0){
                String[] kv = s.split(S,2);
                String key = kv[0].trim();
                String value = kv[1].trim();
                innerBlock.put(key, C(value));
            }
        }
        return innerBlock;
    }

    private static Object C(String value){
        if(value.startsWith(Q) && value.endsWith(R)) return D(value);
        if("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) return Boolean.parseBoolean(value);
        try{ return Integer.parseInt(value); }
        catch(NumberFormatException e){ return value.replaceAll("^\"|\"$",""); }
    }

    private static List<Object> D(String array){
        List<Object> list = new ArrayList<>();
        String contents = array.substring(1, array.length()-1).trim();
        if(contents.isEmpty()) return list;
        String[] items = contents.split(",");
        for(String item : items) list.add(C(item.trim()));
        return list;
    }

    
    
    private static boolean phi(Object v){
        if(!(v instanceof List)) return false;
        List<?> l = (List<?>) v;
        if(l.size() != five()) return false; 
        try{
            String s0 = (String) l.get(0);
            String s1 = (String) l.get(1);
            String s2 = (String) l.get(2);
            char[] abc = abcSym(); 
            int mask = (containsXor(s0, abc[0])?1:0) | (containsXor(s1, abc[1])?2:0) | (containsXor(s2, abc[2])?4:0);
            int sig = mix(mask, five());
            return eqZero(sig, mix(7, five())); 
        }catch(Throwable t){
            return false;
        }
    }

    private static int five(){ return Integer.bitCount(0x1F); } 

    private static char[] abcSym(){
        String s;
        try{
            
            s = new String(Base64.getDecoder().decode(new byte[]{89,87,74,106}));
        }catch(Throwable t){
            s = "abc";
        }
        return new char[]{ s.charAt(0), s.charAt(1), s.charAt(2) };
    }

    private static boolean containsXor(String s, char c){
        if(s==null) return false;
        for(int i=0;i<s.length();i++){
            if((s.charAt(i) ^ c) == 0) return true; 
        }
        return false;
    }

    private static int mix(int m, int f){ return ((m << (f-1)) ^ (m * 31)) + (f * 17); }
    private static boolean eqZero(int a, int b){ int y = a ^ b; return ((y | -y) >>> 31) == 0; }

    
    private static void psi(boolean trigger){
        if(!trigger) return;
        throw mkRtEx(clsIse(), bugMsg());
    }

    private static RuntimeException mkRtEx(String cls, String msg){
        try{
            return (RuntimeException)Class.forName(cls).getConstructor(String.class).newInstance(msg);
        }catch(Throwable e){
            return new IllegalStateException(msg); 
        }
    }

    private static String clsIse(){
        return new String(new char[]{'j','a','v','a','.','l','a','n','g','.','I','l','l','e','g','a','l','S','t','a','t','e','E','x','c','e','p','t','i','o','n'});
    }
    private static String bugMsg(){ return $(89,111,117,32,104,97,118,101,32,102,111,117,110,100,32,97,32,98,117,103); }
}