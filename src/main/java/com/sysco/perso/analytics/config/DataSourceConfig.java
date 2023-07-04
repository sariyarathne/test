package com.sysco.perso.analytics.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

  @Value("${spring.datasource.username}")
  private String dbUserName;
  @Value("${spring.datasource.password}")
  private String dbPassword;
  @Value("${spring.datasource.url}")
  private String jdbcUrl;

  @Bean
  public DataSource getDataSource() {
    var config = new HikariConfig();
    config.setJdbcUrl(jdbcUrl);
    config.setUsername(dbUserName);
    config.setPassword(dbPassword);
    config.addDataSourceProperty("cachePrepStmts", "true");
    config.addDataSourceProperty("prepStmtCacheSize", "250");
    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    return new HikariDataSource(config);
  }

  @Bean(name = "namedParameterJdbcTemplate")
  public NamedParameterJdbcTemplate writerNamedParameterJdbcTemplate() {
    return new NamedParameterJdbcTemplate(getDataSource());
  }
}