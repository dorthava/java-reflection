package edu.school21;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Main {
    public static void main(String[] args) {
        System.out.println("Classes:\n" +
                "  - User\n" +
                "  - Car\n" +
                "---------------------\n" +
                "Enter class name:");
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in))) {
            String className = "edu.school21.classes." + bufferedReader.readLine();
            Class<?> clazz = Class.forName(className);
            Field[] fields = clazz.getDeclaredFields();
            Method[] methods = clazz.getDeclaredMethods();
            String[] fullMethodNames = new String[methods.length];
            System.out.println("fields:");
            for (Field field : fields) {
                System.out.println("        " + field.getType().getSimpleName() + " " + field.getName());
            }
            System.out.println("methods:");
            for (int i = 0; i != methods.length; ++i) {
                String typeName = methods[i].getReturnType().getSimpleName();
                System.out.print("        " + typeName + " ");
                fullMethodNames[i] = methods[i].getName() + "(";
                Class<?>[] parametersInMethod = methods[i].getParameterTypes();
                int count = 0;
                for (Class<?> parameter : parametersInMethod) {
                    if (count > 0) {
                        fullMethodNames[i] += ", ";
                    }
                    fullMethodNames[i] += parameter.getSimpleName();
                    ++count;
                }
                fullMethodNames[i] += ")";
                System.out.println(fullMethodNames[i]);
            }
            System.out.println("---------------------\n" +
                    "Let’s create an object.");
            Object object = null;
            Constructor<?>[] constructors = clazz.getConstructors();
            for (Constructor<?> constructor : constructors) {
                Class<?>[] params = constructor.getParameterTypes();
                if (params.length == 1 && params[0].getSimpleName().equals("String")) {
                    System.out.print(params[0].getSimpleName() + ": ");
                    String parameterWord = bufferedReader.readLine();
                    object = constructor.newInstance(parameterWord);
                    break;
                }
            }
            System.out.println("Object created: " + object);
            System.out.println("---------------------\n" +
                    "Enter name of the field for changing:");
            String fieldLine = bufferedReader.readLine();
            for (Field field : fields) {
                if (field.getName().equals(fieldLine)) {
                    field.setAccessible(true);
                    System.out.println("Enter " + field.getType().getSimpleName() + " value:");
                    field.set(object, parseValue(field.getType(), bufferedReader.readLine()));
                    field.setAccessible(false);
                    break;
                }
            }
            System.out.println("Object updated: " + object +
                    "\n---------------------\n" +
                    "Enter name of the method for call:");
            String methodName = bufferedReader.readLine();
            for(int i = 0; i != methods.length; ++i) {
                if(fullMethodNames[i].equals(methodName)) {
                    Class<?>[] params = methods[i].getParameterTypes();
                    Object[] paramValues = new Object[params.length];
                    for (int j = 0; j < params.length; ++j) {
                        System.out.println("Enter " + params[j].getSimpleName() + " value:");
                        paramValues[j] = parseValue(params[j], bufferedReader.readLine());
                    }
                    Object result = methods[i].invoke(object, paramValues);
                    if (methods[i].getReturnType() != void.class) {
                        System.out.println("Method returned:");
                        System.out.println(result);
                    }
                    break;
                }
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static Object parseValue(Class<?> type, String value) {
        if (type == String.class) {
            return value;
        } else if (type == int.class || type == Integer.class) {
            return Integer.parseInt(value);
        } else if (type == double.class || type == Double.class) {
            return Double.parseDouble(value);
        } else if (type == boolean.class || type == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (type == long.class || type == Long.class) {
            return Long.parseLong(value);
        }
        throw new IllegalArgumentException();
    }
}
