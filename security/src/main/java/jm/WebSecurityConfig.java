package jm;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@ComponentScan(basePackages = {"jm"})
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private UserDetailsService userDetailsServiceImpl;
    private CustomAuthFilter customAuthFilter;

    public WebSecurityConfig(UserDetailsService userDetailsServiceImpl, CustomAuthFilter customAuthFilter) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.customAuthFilter = customAuthFilter;
    }

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
    public FilterRegistrationBean<CustomAuthFilter> authFilter() {
        FilterRegistrationBean<CustomAuthFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(customAuthFilter);
        registrationBean.addUrlPatterns("/rest/api/jmsm/api/*");
        return registrationBean;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
//                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()) //what is that
//                .permitAll()
//                .and()
//                .authorizeRequests()
                .antMatchers("/", "/rest/api/**", "/email/**", "/js/**", "/image/**").permitAll()
                .antMatchers("/admin/**").hasRole("OWNER")
                .antMatchers("/user/**", "/rest/**", "/upload").hasAnyRole("OWNER", "USER")
                .antMatchers("/chooseWorkspace", "/workspace/**", "/avatar", "/avatar/**").authenticated()
                .and()
                .authorizeRequests()
                .antMatchers("/signin", "/password-recovery/**").permitAll()
                .and()
                .formLogin()
                .loginPage("/login").permitAll()
                .successHandler(new JmAuthenticationSuccessHandler())
//                .failureHandler(jmAuthenticationFailureHandler())
                .and()
                .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)
                .and()
                .exceptionHandling().accessDeniedHandler(new JmAccessDeniedHandler());
//                .and()
//                .httpBasic();

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
