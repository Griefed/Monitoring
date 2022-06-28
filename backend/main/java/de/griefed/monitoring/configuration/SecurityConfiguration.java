/* MIT License
 *
 * Copyright (c) 2021 SUK-IT
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.griefed.monitoring.configuration;

import de.griefed.monitoring.ApplicationProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {

  private static final Logger LOG = LogManager.getLogger(SecurityConfiguration.class);

  private final ApplicationProperties APPLICATIONPROPERTIES;

  @Autowired
  public SecurityConfiguration(ApplicationProperties injectedApplicationProperties) {
    this.APPLICATIONPROPERTIES = injectedApplicationProperties;
  }

  @Bean
  @Order(0)
  SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
    switch (APPLICATIONPROPERTIES.getSecuritySetting()) {
      case ALL:
        httpSecurity.authorizeRequests().anyRequest().authenticated().and().formLogin().permitAll();
        break;

      case SETTINGS:
        httpSecurity
            .authorizeRequests()
            .antMatchers("/")
            .permitAll()
            .antMatchers("/css/**")
            .permitAll()
            .antMatchers("/fonts/**")
            .permitAll()
            .antMatchers("/icons/**")
            .permitAll()
            .antMatchers("/img/**")
            .permitAll()
            .antMatchers("/js/**")
            .permitAll()
            .antMatchers("/favicon.gif")
            .permitAll()
            .antMatchers("/favicon.ico")
            .permitAll()
            .antMatchers("/index.html")
            .permitAll()
            .antMatchers("/error")
            .permitAll()
            .antMatchers("/whoops")
            .permitAll()
            .antMatchers("/login")
            .permitAll()
            .antMatchers("/api/v1/hosts")
            .permitAll()
            .antMatchers("/api/v1/settings")
            .permitAll()
            .antMatchers("/api/v1/updates")
            .permitAll()
            .anyRequest()
            .authenticated()
            .and()
            .formLogin()
            .permitAll();
        break;

      case DEACTIVATE:
        httpSecurity.authorizeRequests().antMatchers("/**").permitAll();
        break;

      default:
        LOG.error(
            "Security setting invalid. Defaulting to ALL. Setting must be either ALL, SETTINGS, or DEACTIVATE.");
        httpSecurity.authorizeRequests().anyRequest().authenticated().and().formLogin().permitAll();
        break;
    }

    return httpSecurity.build();
  }
}
