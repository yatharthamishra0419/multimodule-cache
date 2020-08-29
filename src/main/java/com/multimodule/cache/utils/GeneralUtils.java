package com.multimodule.cache.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/*
 * Author : nitin.goyal
 * */
public class GeneralUtils {

  private static Logger logger = LoggerFactory.getLogger(GeneralUtils.class);

  private GeneralUtils() {

  }

  private static String emailRegex =
      "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z"
          + "A-Z]{2,7}$";
  private static Pattern pat = Pattern.compile(emailRegex);

  public static String[] setToArray(Set<String> set) {
    int length = set.size();
    String[] strArray = new String[length];
    int i = 0;
    for (String str : set) {
      strArray[i] = str;
      i++;
    }
    return strArray;
  }

  public static Date addDaysToDate(Date date, int days) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.DAY_OF_MONTH, days);
    return calendar.getTime();
  }

  public static double roundOff(double value, int decimalDigits) {
    if (decimalDigits == 0) {
      return Math.round(value);
    }
    double multiplier = 1.0d;
    for (int i = 0; i < decimalDigits; i++) {
      multiplier *= 10.0;
    }
    return Math.round(value * multiplier) / multiplier;

  }

  public static Date addDaysToCurrentDate(int days) {
    Date date = new Date();
    return addDaysToDate(date, days);
  }

  public static String convertToJsonCompatibleXML(String rawString) {
    String processedString = rawString.replace("\"", "'");
    processedString = processedString.replace("\\", "");
    return processedString;
  }

  public static String convertDateToString(Date date, String format) {
    DateFormat dateFormatter = new SimpleDateFormat(format);
    return dateFormatter.format(date);
  }

  public static Date convertStringToDate(String strDate, String format) throws ParseException {
    DateFormat dateFormatter = new SimpleDateFormat(format);
    return dateFormatter.parse(strDate);
  }

  public static String changeDateFormat(String datestr, String orgFormat, String finalFormat)
      throws ParseException {
    DateFormat dateFormatter = new SimpleDateFormat(orgFormat);
    Date date = dateFormatter.parse(datestr);
    dateFormatter = new SimpleDateFormat(finalFormat);
    return dateFormatter.format(date);

  }

  public static String getStringValue(Object object) {
    return object == null ? null : object.toString();
  }

  public static String getStringValueFromList(Object object, int pos) {
    if (object == null) {
      return "";
    }
    @SuppressWarnings("unchecked") List<String> list = (List<String>) object;
    return list.size() > pos ? list.get(pos) : "";

  }

  public static String getEmptyIfNull(String strIn) {
    return strIn == null ? "" : strIn;
  }





  public static String[] tokenize(String toTokenize, String splitSeqinuence) {
    List<String> list = new ArrayList<>();

    if (toTokenize != null) {
      int splitIndex = toTokenize.indexOf(splitSeqinuence);
      while (splitIndex != -1) {
        String token = toTokenize.substring(0, splitIndex);
        list.add(token);
        toTokenize = toTokenize.substring(splitIndex + 1);
        splitIndex = toTokenize.indexOf(splitSeqinuence);
      }
      list.add(toTokenize);
    }
    return list.toArray(new String[]{});
  }

  public static String replaceString(String source, String replaceThis, String replaceWith) {
    String result = source;
    int isPresent = source.indexOf(replaceThis);
    if (isPresent != -1) {
      result = result.replaceAll(replaceThis, replaceWith);
    }
    return result;
  }

  public static <T, P> void transferMember(Map<T, P> source, Map<T, P> target, T key) {
    transferMember(source, target, key, null);
  }

  public static <T, P> void transferMember(Map<T, P> source, Map<T, P> target, T key,
      P defaultValue) {
    if (source.containsKey(key)) {
      target.put(key, source.get(key));
    } else if (defaultValue != null) {
      target.put(key, defaultValue);
    }
  }

  public static <T> T getFirstElementIfPresent(List<T> list) {
    if (list != null && !list.isEmpty()) {
      return list.get(0);
    } else {
      return null;
    }
  }

  public static <T> T getLastElementIfPresent(List<T> list) {
    if (list != null && !list.isEmpty()) {
      return list.get(list.size() - 1);
    } else {
      return null;
    }
  }

  public static <T> String listToSingleLine(List<T> items, String separator) {
    if (items.isEmpty()) {
      return "";
    }

    StringBuilder sb = new StringBuilder();
    for (T s : items) {
      sb.append(s.toString() + separator);
    }
    return sb.substring(0, sb.length() - separator.length());

  }

  public static <T> String listToCSV(List<T> items) {
    return listToSingleLine(items, ",");

  }

  public static String shrink(String str) {
    return str.replaceAll("\t", " ").replaceAll("\n", " ").replaceAll("\\s+", " ");
  }

  public static String fetchMatchedString(String string, Pattern pattern, Integer groupNo) {
    if (string == null || pattern == null) {
      return null;
    }
    Matcher m = pattern.matcher(string);
    String subString = null;
    if (m.find()) {
      if (groupNo == null || groupNo == 0) {
        subString = m.group();
      } else {
        subString = m.group(groupNo);
      }

    }
    return subString;
  }

  public static List<String> fetchMatchedStrings(String string, Pattern pattern, Integer groupNo) {
    if (string == null || pattern == null) {
      return null;
    }
    Matcher m = pattern.matcher(string);
    List<String> subStrings = new ArrayList<>();
    while (m.find()) {
      if (groupNo == null || groupNo == 0) {
        subStrings.add(m.group());
      } else {
        subStrings.add(m.group(groupNo));
      }

    }
    return subStrings;
  }

  public static String escapeNewLine(String string) {
    string = string.replaceAll("\r\n", " ");
    return string.replaceAll("\n", " ");
  }

  public static <T> boolean isNullOrEmpty(List<T> list) {
    return (list == null || list.isEmpty());
  }

  public static <T> boolean isNullOrEmpty(T[] array) {
    return (array == null || array.length == 0);
  }

  public static boolean isNullOrEmpty(Object object) {
    if(object instanceof String){
      return isNullOrEmpty((String)object);
    }
    if(object instanceof StringBuilder){
      return isNullOrEmpty((StringBuilder)object);
    }
    if(object instanceof StringBuffer){
      return isNullOrEmpty((StringBuffer)object);
    }
    return (object == null);
  }

  public static <T, P> boolean isNullOrEmpty(Map<T, P> map) {
    return (map == null || map.isEmpty());
  }

  public static String formatDate2(String date) {
    if (date == null || "".equals(date)) {
      return "";
    }
    String newDate = "";
    try {
      newDate = changeDateFormat(date, "yyyy-MM-dd", "dd/MM/yyyy");
    } catch (ParseException e) {
      logger.error("Parser Exception came for date : " + date, e);
    }
    return newDate;
  }



  public static Map<String, String> getHashMapFromString(String value) {
    value = value.substring(1, value.length() - 1);
    String[] keyValuePairs = value.split(",");
    Map<String, String> map = new LinkedHashMap<>();

    for (String pair : keyValuePairs) {
      String[] entry = pair.split("=");
      map.put(entry[0].trim(), entry[1].trim());
    }

    return map;
  }

  public static boolean isValidEmail(String email) {

    if (email == null) {
      return false;
    }
    return pat.matcher(email).matches();
  }

  public static String encodeQueryParams(String url) {
    String res = "";
    if (!isNullOrEmpty(url)) {
      try {
        int index = url.indexOf('?');
        if (index > 0) {
          String urlPrefix = url.substring(0, index);
          String queryParams = url.substring(index + 1);
          res = urlPrefix + "?" + URLEncoder.encode(queryParams, "UTF-8");
        } else {
          res = url;
        }
      } catch (Exception e) {
        logger.error("Exception came while encoding URL : " + url, e);
      }
    }
    return res;
  }

  public static boolean isEmpty(Object obj) {
    if (obj == null) {
      return true;
    }
    if (obj instanceof String) {
      return ((String) obj).trim().isEmpty();
    } else if (obj instanceof Short) {
      return (Short) obj == (short) 0;
    } else if (obj instanceof Integer) {
      return (Integer) obj == 0;
    } else if (obj instanceof Long) {
      return (Long) obj == (long) 0;
    } else if (obj instanceof List) {
      return ((List) obj).isEmpty();
    } else if (obj instanceof Map) {
      return ((Map) obj).isEmpty();
    } else if (obj instanceof Set) {
      return ((Set) obj).isEmpty();
    }
    return false;
  }

  public static boolean isNotNullOrEmpty(Integer obj) {
    return obj != null && obj > 0;
  }

  public static boolean isNotNullOrEmpty(String obj) {
    return obj != null && !obj.isEmpty();
  }

  public static boolean isNullOrEmpty(String obj) {
    return obj == null || obj.isEmpty();
  }
  public static boolean isNullOrEmpty(StringBuilder obj) {
    return obj == null || obj.length()==0;
  }
  public static boolean isNullOrEmpty(StringBuffer obj) {
    return obj == null || obj.length()==0;
  }

  public static boolean isNotNullOrEmpty(Character obj) {
    return obj != null;
  }

  public static boolean isNotNullOrEmpty(Collection<?> collection) {
    return collection != null && !collection.isEmpty();
  }

  public static boolean isNotNull(Integer obj) {
    return obj != null;
  }

  public static boolean isNotNull(Object obj) {
    return obj != null;
  }

  public static boolean isNotNull(Float obj) {
    return obj != null;
  }

  public static boolean isNotNull(Boolean obj) {
    return obj != null;
  }

  public static String convertToString(Object object) {
    if (object == null) {
      return "";
    }
    return object.toString();
  }

  public static Long getTimeFromDate(java.sql.Date date) {
    if (date == null) {
      return 0L;
    }
    return date.getTime();
  }

  public static String removeTrailingZeroes(String s) {
    StringBuilder sb = new StringBuilder(s);
    while (s.length() > 0 && sb.charAt(sb.length() - 1) == '0') {
      sb.setLength(sb.length() - 1);
    }
    return sb.toString();
  }

  public static String convertToStringRepresentationOfBoolean(Boolean bol) {
    if (isNotNull(bol)) {
      return bol.toString();
    }
    return "false";
  }




  public static String[] removeDuplicates(String[] str) {
    Set<String> distinctString = new LinkedHashSet<>(Arrays.asList(str));
    return distinctString.toArray(new String[0]);
  }

  public static List<String> removeDuplicates(List<String> str) {
    if (str == null) {
      return str;
    }
    return str.stream().distinct().collect(Collectors.toList());
  }

  public static void replaceMultipleSpaceWithSingleSpaceInList(List<String> stringList) {
    if (stringList == null) {
      return;
    }
    ListIterator<String> iterator = stringList.listIterator();
    while (iterator.hasNext()) {
      iterator.set(replaceMultipleSpaceWithSingleSpace(iterator.next()));
    }
  }

  public static String replaceMultipleSpaceWithSingleSpace(String string) {
    if (string == null || string.isEmpty()) {
      return string;
    }
    return string.trim().replaceAll("(\\s)+", " ");
  }

  public static List<String> filterElements(List<String> stringList) {
    return stringList.stream().filter(s -> s != null && !s.trim().isEmpty())
        .collect(Collectors.toList());
  }

  public static List<String> getSubList(List<String> stringList, int start, int end) {
    if (stringList == null || stringList.isEmpty()) {
      return stringList;
    }
    return stringList.subList(start, Math.min(end, stringList.size()));
  }



  public static Object copyObject(Object from, Object to) {
    Class toClass;
    Field toField;
    for (Field fromField : from.getClass().getDeclaredFields()) {
      fromField.setAccessible(true);
      toClass = to.getClass();

      toField = null;
      while (toClass != Object.class) {
        try {
          toField = toClass.getDeclaredField(fromField.getName());
          break;
        } catch (NoSuchFieldException e) {
          toClass = toClass.getSuperclass();
        }
      }

      if (toField != null) {
        try {
          toField.setAccessible(true);
          toField.set(to, fromField.get(from));
        } catch (IllegalAccessException | IllegalArgumentException e) {

          //special handling for string
          if (fromField.getType() != String.class && toField.getType() == String.class) {
            try {
              toField.set(to, fromField.get(from).toString());
            } catch (IllegalAccessException e2) {
              logger.error("Error in copying object");
            }
          }
        }
      }
    }
    return to;
  }

  public static String wrapToString(Object obj) {
    if (isEmpty(obj)) {
      return null;
    }
    return obj.toString();
  }

  public static List<String> wrapToList(String... arr) {
    if (arr != null) {
      return Arrays.asList(arr);
    } else {
      return new ArrayList<>(1);
    }
  }

  public static List<String> removeQuotes(List<String> stringList) {
    List<String> response = new ArrayList<>();
    if (stringList != null) {
      for (String t : stringList) {
        String s = t.replaceAll("\'", "");
        response.add(s);
      }
    }
    return response;
  }

  public static Boolean isArabicString(String inputString) {
    if (inputString != null) {
      for (char ch : inputString.toCharArray()) {
        if (Character.UnicodeBlock.of(ch) == Character.UnicodeBlock.ARABIC) {
          return true;
        }
      }
    }
    return false;
  }

  public static Boolean isEnglishString(String inputString) {
    if (inputString != null) {
      for (char ch : inputString.toCharArray()) {
        if (ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z') {
          return true;
        }
      }
    }
    return false;
  }

  public enum Seperator {
    SPACE(" "),
    HYPHEN("-");

    private String text;

    Seperator(String text) {
      this.text = text;
    }

    public String getText() {
      return this.text;
    }
  }
}

