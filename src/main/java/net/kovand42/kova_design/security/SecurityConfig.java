package net.kovand42.kova_design.security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;

import javax.sql.DataSource;

@EnableWebSecurity
public class SecurityConfig
        extends WebSecurityConfigurerAdapter {
    private static final String AUTHORITIES_BY_USERNAME =
            "select username, 'user' as authorities" +
                    " from users where username= ?";
;
    private static final String USERS_BY_USERNAME =
            "select username, password, enabled" +
                    " from users where username = ?";
    @Bean
    JdbcDaoImpl jdbcDaoImpl(DataSource dataSource) {
        JdbcDaoImpl impl = new JdbcDaoImpl();
        impl.setDataSource(dataSource);
        impl.setUsersByUsernameQuery(USERS_BY_USERNAME);
        impl.setAuthoritiesByUsernameQuery(AUTHORITIES_BY_USERNAME);
        return impl;
    }
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .mvcMatchers("/images/**")
                .mvcMatchers("/css/**")
                .mvcMatchers("/js/**");
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin().loginPage("/login").and().logout().logoutSuccessUrl("/")
                .and().authorizeRequests()
                .mvcMatchers("/applications/**").hasAnyAuthority("user", "admin");
    }
}
