/**
 *  OpenAtlasForAndroid Project
The MIT License (MIT) Copyright (OpenAtlasForAndroid) 2015 Bunny Blue,achellies

Permission is hereby granted, free of charge, to any person obtaining a copy of this software
and associated documentation files (the "Software"), to deal in the Software 
without restriction, including without limitation the rights to use, copy, modify, 
merge, publish, distribute, sublicense, and/or sell copies of the Software, and to 
permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies 
or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
@author BunnyBlue
 * **/
package com.openAtlas.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class ClassUtils {
    public static final String INNER_CLASS_SEPARATOR;
    public static final char INNER_CLASS_SEPARATOR_CHAR = '$';
    public static final String PACKAGE_SEPARATOR;
    public static final char PACKAGE_SEPARATOR_CHAR = '.';
    public static final String STRING_EMPTY = "";
    private static final Map<String, String> abbreviationMap;
    private static final Map<Class<?>, Class<?>> primitiveWrapperMap;
    private static final Map<String, String> reverseAbbreviationMap;
    private static final Map<Class<?>, Class<?>> wrapperPrimitiveMap;

    static {
        PACKAGE_SEPARATOR = String.valueOf(PACKAGE_SEPARATOR_CHAR);
        INNER_CLASS_SEPARATOR = String.valueOf(INNER_CLASS_SEPARATOR_CHAR);
        primitiveWrapperMap = new HashMap();
        primitiveWrapperMap.put(Boolean.TYPE, Boolean.class);
        primitiveWrapperMap.put(Byte.TYPE, Byte.class);
        primitiveWrapperMap.put(Character.TYPE, Character.class);
        primitiveWrapperMap.put(Short.TYPE, Short.class);
        primitiveWrapperMap.put(Integer.TYPE, Integer.class);
        primitiveWrapperMap.put(Long.TYPE, Long.class);
        primitiveWrapperMap.put(Double.TYPE, Double.class);
        primitiveWrapperMap.put(Float.TYPE, Float.class);
        primitiveWrapperMap.put(Void.TYPE, Void.TYPE);
        wrapperPrimitiveMap = new HashMap();
        for (Class cls : primitiveWrapperMap.keySet()) {
            Class cls2 = primitiveWrapperMap.get(cls);
            if (!cls.equals(cls2)) {
                wrapperPrimitiveMap.put(cls2, cls);
            }
        }
        abbreviationMap = new HashMap();
        reverseAbbreviationMap = new HashMap();
        addAbbreviation("int", "I");
        addAbbreviation("boolean", "Z");
        addAbbreviation("float", "F");
        addAbbreviation("long", "J");
        addAbbreviation("short", "S");
        addAbbreviation("byte", "B");
        addAbbreviation("double", "D");
        addAbbreviation("char", "C");
    }

    private static void addAbbreviation(String str, String str2) {
        abbreviationMap.put(str, str2);
        reverseAbbreviationMap.put(str2, str);
    }

    public static String getShortClassName(Object obj, String str) {
        return obj == null ? str : getShortClassName(obj.getClass());
    }

    public static String getShortClassName(Class<?> cls) {
        return cls == null ? STRING_EMPTY : getShortClassName(cls.getName());
    }

    public static String getShortClassName(String str) {
        int i = 0;
        if (str == null) {
            return STRING_EMPTY;
        }
        if (str.length() == 0) {
            return STRING_EMPTY;
        }
        String str2;
        StringBuilder stringBuilder = new StringBuilder();
        if (str.startsWith("[")) {
            while (str.charAt(0) == '[') {
                str = str.substring(1);
                stringBuilder.append("[]");
            }
            if (str.charAt(0) == 'L' && str.charAt(str.length() - 1) == ';') {
                str = str.substring(1, str.length() - 1);
            }
        }
        if (reverseAbbreviationMap.containsKey(str)) {
            str2 = reverseAbbreviationMap.get(str);
        } else {
            str2 = str;
        }
        int lastIndexOf = str2
                .lastIndexOf(Constants.LOGIN_HANDLER_KEY_SHOWMAINPAGE);
        if (lastIndexOf != -1) {
            i = lastIndexOf + 1;
        }
        i = str2.indexOf(Constants.LOGIN_HANDLER_KEY_CAIPIAO_MYORDER, i);
        str2 = str2.substring(lastIndexOf + 1);
        if (i != -1) {
            str2 = str2.replace(INNER_CLASS_SEPARATOR_CHAR,
                    PACKAGE_SEPARATOR_CHAR);
        }
        return str2 + stringBuilder;
    }

    public static String getSimpleName(Class<?> cls) {
        return cls == null ? STRING_EMPTY : cls.getSimpleName();
    }

    public static String getSimpleName(Object obj, String str) {
        return obj == null ? str : getSimpleName(obj.getClass());
    }

    public static String getPackageName(Object obj, String str) {
        return obj == null ? str : getPackageName(obj.getClass());
    }

    public static String getPackageName(Class<?> cls) {
        return cls == null ? STRING_EMPTY : getPackageName(cls.getName());
    }

    public static String getPackageName(String str) {
        if (str == null || str.length() == 0) {
            return STRING_EMPTY;
        }
        while (str.charAt(0) == '[') {
            str = str.substring(1);
        }
        if (str.charAt(0) == 'L' && str.charAt(str.length() - 1) == ';') {
            str = str.substring(1);
        }
        int lastIndexOf = str
                .lastIndexOf(Constants.LOGIN_HANDLER_KEY_SHOWMAINPAGE);
        return lastIndexOf == -1 ? STRING_EMPTY : str.substring(0, lastIndexOf);
    }

    public static List<Class<?>> getAllSuperclasses(Class<?> cls) {
        if (cls == null) {
            return null;
        }
        List<Class<?>> arrayList = new ArrayList();
        for (Class superclass = cls.getSuperclass(); superclass != null; superclass = superclass
                .getSuperclass()) {
            arrayList.add(superclass);
        }
        return arrayList;
    }

    public static List<Class<?>> getAllInterfaces(Class<?> cls) {
        if (cls == null) {
            return null;
        }
        Collection linkedHashSet = new LinkedHashSet();
        getAllInterfaces(cls, (HashSet<Class<?>>) linkedHashSet);
        return new ArrayList(linkedHashSet);
    }

    private static void getAllInterfaces(Class<?> cls, HashSet<Class<?>> hashSet) {
        while (cls != null) {
            for (Class cls2 : cls.getInterfaces()) {
                if (hashSet.add(cls2)) {
                    getAllInterfaces(cls2, hashSet);
                }
            }
            cls = cls.getSuperclass();
        }
    }

    public static List<Class<?>> convertClassNamesToClasses(List<String> list) {
        if (list == null) {
            return null;
        }
        List<Class<?>> arrayList = new ArrayList(list.size());
        for (String str : list) {
            try {
                arrayList.add(Class.forName(str));
            } catch (Exception e) {
                arrayList.add(null);
            }
        }
        return arrayList;
    }

    public static List<String> convertClassesToClassNames(List<Class<?>> list) {
        if (list == null) {
            return null;
        }
        List<String> arrayList = new ArrayList(list.size());
        for (Class cls : list) {
            if (cls == null) {
                arrayList.add(null);
            } else {
                arrayList.add(cls.getName());
            }
        }
        return arrayList;
    }

    public static Class<?> getClass(ClassLoader classLoader, String str,
            boolean z) throws ClassNotFoundException {
        ClassNotFoundException e;
        int lastIndexOf;
        try {
            return abbreviationMap.containsKey(str) ? Class.forName(
                    "[" + (abbreviationMap.get(str)), z, classLoader)
                    .getComponentType() : Class.forName(toCanonicalName(str),
                    z, classLoader);
        } catch (ClassNotFoundException e2) {
            e = e2;

            lastIndexOf = str
                    .lastIndexOf(Constants.LOGIN_HANDLER_KEY_SHOWMAINPAGE);

            if (lastIndexOf != -1) {
                try {
                    return getClass(
                            classLoader,
                            str.substring(0, lastIndexOf)
                                    + INNER_CLASS_SEPARATOR_CHAR
                                    + str.substring(lastIndexOf + 1), z);
                } catch (ClassNotFoundException e3) {
                    throw e;
                }
            }
            throw e;
        }
    }

    public static Class<?> getClass(ClassLoader classLoader, String str)
            throws ClassNotFoundException {
        return getClass(classLoader, str, true);
    }

    public static Class<?> getClass(String str) throws ClassNotFoundException {
        return getClass(str, true);
    }

    public static Class<?> getClass(String str, boolean z)
            throws ClassNotFoundException {
        ClassLoader contextClassLoader = Thread.currentThread()
                .getContextClassLoader();
        if (contextClassLoader == null) {
            contextClassLoader = ClassUtils.class.getClassLoader();
        }
        return getClass(contextClassLoader, str, z);
    }

    private static String toCanonicalName(String str) {
        String deleteWhitespace = deleteWhitespace(str);
        if (deleteWhitespace == null) {
            throw new NullPointerException("className must not be null.");
        } else if (!deleteWhitespace.endsWith("[]")) {
            return deleteWhitespace;
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            String str2 = deleteWhitespace;
            while (str2.endsWith("[]")) {
                deleteWhitespace = str2.substring(0, str2.length() - 2);
                stringBuilder.append("[");
                str2 = deleteWhitespace;
            }
            deleteWhitespace = abbreviationMap.get(str2);
            if (deleteWhitespace != null) {
                stringBuilder.append(deleteWhitespace);
            } else {
                stringBuilder.append("L").append(str2).append(";");
            }
            return stringBuilder.toString();
        }
    }

    public static Class<?>[] toClass(Object... objArr) {
        int i = 0;
        if (objArr == null) {
            return null;
        }
        if (objArr.length == 0) {
            return new Class<?>[0];
        }
        Class<?>[] clsArr = new Class<?>[objArr.length];
        while (i < objArr.length) {
            clsArr[i] = objArr[i] == null ? null : objArr[i].getClass();
            i++;
        }
        return clsArr;
    }

    public static String deleteWhitespace(String str) {
        if (isEmpty(str)) {
            return str;
        }
        int length = str.length();
        char[] cArr = new char[length];
        int i = 0;
        int i2 = 0;
        while (i < length) {
            int i3;
            if (Character.isWhitespace(str.charAt(i))) {
                i3 = i2;
            } else {
                i3 = i2 + 1;
                cArr[i2] = str.charAt(i);
            }
            i++;
            i2 = i3;
        }
        return i2 != length ? new String(cArr, 0, i2) : str;
    }

    public static boolean isEmpty(CharSequence charSequence) {
        return charSequence == null || charSequence.length() == 0;
    }
}
