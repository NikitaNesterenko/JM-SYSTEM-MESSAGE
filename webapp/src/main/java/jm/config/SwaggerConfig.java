package jm.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi botOpenApi() {
        String[] path = {"/rest/api/bot/**"};
        return getGroupedOpenApi(path, "bot");
    }

    @Bean
    public GroupedOpenApi channelOpenApi() {
        String[] path = {"/rest/api/channels/**"};
        return getGroupedOpenApi(path, "channel");
    }

    @Bean
    public GroupedOpenApi conversationOpenApi() {
        String[] path = {"/rest/api/conversations/**"};
        return getGroupedOpenApi(path, "conversation");
    }

    @Bean
    public GroupedOpenApi createWorkspaceOpenApi() {
        String[] path = {"/rest/api/createWorkSpace/**"};
        return getGroupedOpenApi(path, "create workspace");
    }

    @Bean
    public GroupedOpenApi directMessageOpenApi() {
        String[] path = {"/rest/api/direct_messages/**"};
        return getGroupedOpenApi(path, "direct message");
    }

    @Bean
    public GroupedOpenApi imageOpenApi() {
        String[] path = {"/images/**"};
        return getGroupedOpenApi(path, "image");
    }

    @Bean
    public GroupedOpenApi inviteTokenOpenApi() {
        String[] path = {"/rest/api/invites/**"};
        return getGroupedOpenApi(path, "invite");
    }

    @Bean
    public GroupedOpenApi messageOpenApi() {
        String[] path = {"/rest/api/messages/**"};
        return getGroupedOpenApi(path, "message");
    }

    @Bean
    public GroupedOpenApi pluginOpenApi() {
        String[] path = {"/rest/plugin/**"};
        return getGroupedOpenApi(path, "plugin");
    }

    @Bean
    public GroupedOpenApi threadOpenApi() {
        String[] path = {"/rest/api/threads/**"};
        return getGroupedOpenApi(path, "thread");
    }

    @Bean
    public GroupedOpenApi userOpenApi() {
        String[] path = {"/rest/api/users/**"};
        return getGroupedOpenApi(path, "user");
    }

    @Bean
    public GroupedOpenApi workspaceOpenApi() {
        String[] path = {"/rest/api/workspaces/**"};
        return getGroupedOpenApi(path, "workspace");
    }

    @Bean
    public OpenAPI customOpenAPI() {
        Contact contact = new Contact()
                .name("TechPrimers")
                .url("https://github.com/NikitaNesterenko/JM-SYSTEM-MESSAGE")
                .email("https://app.slack.com/client/T2AEH901M/DMC1ST5C1/thread/GJFSRQKUN-1578001020.108900");

        License license = new License()
                .name("Apache License Version 2.0")
                .url("https://www.apache.org/licesen.html");

        Info info = new Info()
                .title("JM System message")
                .version("1.0")
                .contact(contact)
                .license(license);

        return new OpenAPI()
                .components(
                        new Components().addSecuritySchemes("basicScheme",
                                new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("basic"))
                )
                .info(info);
    }

    private GroupedOpenApi getGroupedOpenApi(String[] path, String channels) {
        return GroupedOpenApi
                .builder()
                .setGroup(channels)
                .pathsToMatch(path)
                .build();
    }
}
