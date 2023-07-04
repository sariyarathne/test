package com.sysco.perso.analytics.testutils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import liquibase.repackaged.com.opencsv.CSVReader;
import liquibase.repackaged.com.opencsv.CSVReaderBuilder;
import liquibase.repackaged.com.opencsv.exceptions.CsvException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FileUtil {

  public List<String> readCSVFile(String filePath) {
    try {
      FileReader filereader = new FileReader(filePath);
      CSVReader csvReader = new CSVReaderBuilder(filereader)
              .withSkipLines(1)
              .build();

      List<String[]> allData = csvReader.readAll();
      return allData.stream().map(data -> data[2]).toList();

    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (CsvException e) {
      throw new RuntimeException(e);
    }

  }

  public <T> List<T> readJsonArray(final String filePath, final Class<T[]> clazz) {
    List<T> objects = Collections.emptyList();
    try {
      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

      objects = Arrays.asList(
              mapper.readValue(FileUtil.class.getResourceAsStream(filePath),clazz));

      objects.forEach(System.out::println);

    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return objects;

  }

  public <T> T readJson(final String filePath, final Class<T> clazz) {
    T object = null;
    try {
      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
      object = mapper.readValue(FileUtil.class.getResourceAsStream(filePath),clazz);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return object;
  }
}
