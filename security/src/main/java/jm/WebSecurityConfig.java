package jm;

import jm.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;


@Configuration
@EnableWebSecurity
@ComponentScan(basePackages = {"jm"})
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsService userDetailsServiceImpl;

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsServiceImpl).passwordEncoder(passwordEncoder());
    }

    @Bean
    @SuppressWarnings("deprecation")
    public static NoOpPasswordEncoder passwordEncoder() {
        return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public AuthenticationSuccessHandler simpleAuthenticationSuccessHandler() {
        return new SimpleUrlAuthenticationSuccessHandler();
    }

    @Bean
    public AuthenticationSuccessHandler jmAuthenticationSuccessHandler() {
        return new JmAuthenticationSuccessHandler();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable();

        http
                .authorizeRequests()
                .requestMatchers(PathRequest.toStaticResources()
                        .atCommonLocations())
                .permitAll();

        http
                .authorizeRequests()
                .antMatchers("/workspace/login").permitAll();

        http
                .authorizeRequests()
                .antMatchers("/", "/signin").permitAll();


        // Config for Login Form

        http
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin()//
                // Submit URL of login page.
                //        .loginProcessingUrl("/perform_login") // Submit URL
                //        .loginPage("/login")//
                .usernameParameter("username")//
                .passwordParameter("password")
                .successHandler(jmAuthenticationSuccessHandler())
                //    .failureUrl("/signin")//
                .and()
                .logout()
                .logoutUrl("/perform_logout")
                .permitAll()
                .invalidateHttpSession(true)
                .and()
                .httpBasic();

        /*
        http
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .and()
                .logout()
                .permitAll()
                .and()
                .httpBasic();

*/
    }

}
