# Spring Framework (back-end) - Documentação da API REST com Open API 3.0 e versionamento do Banco de Dados com Flyway

## 1. Documentação da API REST com Open API 3.0
A documentação é uma parte essencial da construção de APIs REST. Nesta aula, iremos utilizar a biblioteca SpringDoc, que simplifica a geração e manutenção da documentação de API com base na especificação OpenAPI 3 [2] para aplicações Spring Boot [1].

### Configurando a dependência springdoc-openapi
O Spring Boot na versão 3.x [1] necessita a versão 2 da biblioteca springdoc-openapi [3]. Para isso basta adicionar a dependência no arquivo **pom.xml**, no caso desde projeto com o Spring Boot na versão 3.5.4:

``` xml
<!--... -->
	<dependencies>
		<dependency>
			<groupId>org.springdoc</groupId>
			<artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
			<version>2.8.9</version>
		</dependency>
	</dependencies>
<!--... -->
```
A versão da biblioteca **SpringDoc** pode variar conforme a versão do Spring Boot utilizado no projeto.

## Acesso à documentação gerada

Após adicionada a biblioteca no arquivo pom.xml basta executar a aplicação. A documentação é gerada automaticamente, para visualizar a documentação basta acessar a url `/v3/api-docs`. No caso deste projeto que está sendo executado na porta *8080* o endereço será:

[http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

A documentação OpenAPI por padrão está no formato JSON. Para visualizar no formato _yaml_ basta acessar a url:

[http://localhost:8080/v3/api-docs.yaml](http://localhost:8080/v3/api-docs.yaml)
 
É possível ainda mudar a url da documentação _springdoc.api-docs_, alterando suas um parâmetro no arquivo de propriedades do projeto. No caso do projeto **server** as propriedades estão no arquivo **application.yml**:

```yml
springdoc:
	api-docs:
		path: /api-docs
```
Agora a documentação está disponível na url:

```plaintext
http://localhost:8080/api-docs
```
## Integração com o Swagger UI

Apesar da documentação gerada com a especificação OpenAPI 3 fornecer detalhes da estrutura da API REST desenvolvida a visualização apenas no formato JSON pode ser melhorada. Por isso é possível integrar o _springdoc-openapi_ com o **Swagger UI** [6], assim, além de visualizar a documentação também será possível testar os *end-points* da API. A biblioteca  _springdoc-openapi_  já inclui o **Swagger UI**, que pode ser acessado na url:


[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

## Adicionando autenticação 

O próximo passo é configurar a autenticação no Swagger, para poder testar os *endpoints* que necessitam que o usuário esteja autenticado. O primeiro passo é permitir a autenticação do usuário, pois como o *endpoint* `/login` não foi criado como uma classe *controller* e sim gerado pelo Spring Security ele não é mapeado de maneira automática.
Será criada a classe `AuthControllerDoc` dentro do pacote **br.edu.utfpr.pb.pw45s.server.controller**, que terá papel apenas para documentar a URL `/login`. Toda a lógica continuará sendo tratada pelo Spring Security.

```java
package br.edu.utfpr.pb.pw45s.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication")
@RestController
@RequestMapping
public class AuthControllerDoc {

    @Operation(
            summary = "Perform login",
            description = "Spring Security authentication endpoint. Accepts username and password and returns a JWT token.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login successful",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(example = "{\"token\": \"jwt-token-here\"}"))),
                    @ApiResponse(responseCode = "401", description = "Invalid credentials")
            }
    )
    @PostMapping("/login")
    public void login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Login credentials",
                    required = true,
                    content = @Content(schema = @Schema(example = "{\"username\": \"user\", \"password\": \"123\"}"))
            )
            @RequestBody(required = true) Object body) {
        // This method is not actually executed.
        // Spring Security intercepts the request before it reaches this controller.
        // It is only defined here for Swagger documentation purposes.
        throw new IllegalStateException("This method is only for Swagger documentation.");
    }
}
```

Após a autenticação finalizada será possível utilizar o *token* JWT obtido nas requisições, mas primeiro é necessário configurar o Swagger para disponibilizar o botão de Autorização. Para isso será criada a classe `OpenApiConfig`:

```java
package br.edu.utfpr.pb.pw45s.server.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Minha API")
                        .version("v1")
                        .description("Documentação da API com autenticação JWT"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .name("bearerAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
```

## 2. Versionamento do banco de dados com Flyway

### Flyway

  O [Flyway](https://flywaydb.org/) [4] é uma biblioteca utilizada para migração de banco de dados. Qualquer alteração no *schema* do projeto podem facilmente ser controladas e auditadas utilizando o Flyway. Com as configurações padrão, durante a primeira execução da aplicação, é criada uma tabela automaticamente no banco de dados chamada **flyway_schema_history** em que serão armazenadas informações para o controle das versões do *schema* do projeto.

### Configurações iniciais
Esse exemplo utiliza um projeto SpringBoot utilizando o Maven para gerenciamento das dependências. As bibliotecas serão adicionadas no arquivo **pom.xml**. A biblioteca abaixo encontra-se no [Repositório Maven](http://mvnrepository.com/) e é necessária para o funcionamento do Flyway.

```xml
<!--... -->
	<dependencies>
		<dependency>
			<groupId>org.flywaydb</groupId>
			<artifactId>flyway-core</artifactId>
		</dependency>
	<dependencies>
<!--... -->
```
Na sequência será realizada a configuração na classe principal da aplicação **ServerApplication**.

```java
//...
@SpringBootApplication
public  class  ServerApplication {
	//...
	@Bean
	public  static  BeanFactoryPostProcessor  dependsOnPostProcessor() {
		return bf -> {

			// Let beans that need the database depend on the DatabaseStartupValidator
			// like the JPA EntityManagerFactory or Flyway String[] flyway = bf.getBeanNamesForType(Flyway.class);

			Stream.of(flyway)
				.map(bf::getBeanDefinition)
				.forEach(it -> it.setDependsOn("databaseStartupValidator"));
			String[] jpa = bf.getBeanNamesForType(EntityManagerFactory.class);
			Stream.of(jpa)
				.map(bf::getBeanDefinition)
				.forEach(it ->  it.setDependsOn("databaseStartupValidator"));
		};
	}

	@Bean
	public  DatabaseStartupValidator  databaseStartupValidator(DataSource  dataSource) {
		var  dsv = new  DatabaseStartupValidator();
		dsv.setDataSource(dataSource);
		dsv.setTimeout(120);
		dsv.setInterval(7);
		// dsv.setValidationQuery(DatabaseDriver.POSTGRESQL.getValidationQuery());
		dsv.afterPropertiesSet();
		return dsv;
	}
	//...
}
```
O próximo passo é configurar as credenciais de acesso ao banco de dados no arquivo de configuração **application.yml**. O banco de dados utilizado neste exemplo foi o H2, mas pode ser alterado para utilizar qualquer outro SGBD (PostgreSQL, MongoDB, MySQL, MariaDB, Microsoft SQL Server, entre outros). O arquivo está dividido em *profiles* sendo que o profile ativo é o **dev** cada conjunto de '- - - ' delimita um profile.

``` yml
spring:
    profiles:
        active: dev
    datasource:
	    generate-unique-name: false
    h2:
        console:
            enabled: true
            path: /h2-console
    jpa:
        properties:
            javax:
                persistence:
                    validation:
                        mode: none
            hibernate:
                format_sql: false
                show-sql: true
    flyway:
        baseline-on-migrate: true
    mvc:
        pathmatch:
            matching-strategy: ant_path_matcher
---
spring:
  config:
    activate:
      on-profile: prod
  datasource:
    url: jdbc:h2:mem:pw26s-dev
  jpa:
    hibernate:
      ddl-auto: none
  h2:
    console:
      enabled: false
  flyway:
    locations: classpath:/db/prod
---
spring:
  config:
    activate:
      on-profile: dev
  datasource:
    url: jdbc:h2:mem:pw26s-dev
  jpa:
    hibernate:
      ddl-auto: none
  flyway:
    locations: classpath:/db/dev
---
spring:
  config:
    activate:
      on-profile: test
  jpa:
    hibernate:
      ddl-auto: create-drop
  flyway:
    locations: classpath:/db/test
```
Após as configurações iniciais é necessário criar a pasta em que serão armazenados os *scripts* SQL para criação/alteração dos *schemas*. O caminho padrão é: **src/main/resources/db/migration**. Mas nesse projeto esse caminho foi editado no aquivo **application.yml**, sendo que para cada *profile* de execução foi criado um caminho diferente para armazenar os *scripts*. Por exemplo, **flyway: locations: classpath:/db/dev** é o caminho dos *scripts* para o *profile*  **dev** utilizado para o desenvolvimento.

Para nomear os arquivos de *script* deve ser adotado o padrão da biblioteca, o qual também possui uma ordem de execução, tudo está descrito na [documentação do Flyway](https://flywaydb.org/documentation/concepts/migrations.html#naming-1) [5].

### Funcionamento

 Após criados os arquivos de *script* SQL nos diretórios e nomeados conforme a documentação, será possível realizar a primeira migração, a qual contém o *script* inicial para criação do banco de dados. Executando o projeto as migrações serão executadas na ordem do versionamento e para cada script executado será adicionado um registro na tabela **flyway_schema_history**.

A tabela **flyway_schema_history** contém uma chave primária, a versão do banco de dados, a descrição, o tipo do *script* (geralmente SQL), o nome do arquivo do script, o _checksum_ do arquivo de *script*. Nessa tabela também é exibido o usuário do banco de dados que foi utilizado para executar o *script*, a data de execução, o tempo de execução e por fim um valor booleano indicando se a migração ocorreu com sucesso.

O *framework* Flyway executa os passos abaixo para validar o banco de dados da aplicação:

1. Verifica se o banco de dados possui a tabela **flyway_schema_history**, caso ela não exista é criada.

2. Busca no *classpath* da aplicação por arquivos contendo migrações.

3. Compara cada arquivo de migração encontrado com os dados existentes na tabela de histórico. Se o número de versão do arquivo for menor que o da última atualização existente no banco de dados, eles são ignorados.

4. Caso existam migrações para serem executadas o framework coloca em fila. As migrações são executadas da menor versão para maior.

5. Cada migração é executada e a tabela **flyway_schema_history** é atualizada.

# Referências

[1] Spring Framework, https://spring.io/

[2] Open API 3, https://spec.openapis.org/oas/v3.1.0

[3] Spring Doc, https://github.com/springdoc/springdoc-openapi/

[4] Flyway, https://flywaydb.org/

[5] Flyway Docs, https://flywaydb.org/documentation/concepts/migrations.html#naming-1  

[6] Swagger, https://swagger.io/
