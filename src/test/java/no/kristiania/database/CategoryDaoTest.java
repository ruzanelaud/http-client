package no.kristiania.database;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class CategoryDaoTest {

    private ProductCategoryDao categoryDao;
    private Random random = new Random();

    @BeforeEach
    void setUp() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        Flyway.configure().dataSource(dataSource).load().migrate();
        categoryDao = new ProductCategoryDao(dataSource);
    }

    @Test
    void shouldListAllCategories() throws SQLException {
        ProductCategory category1 = exampleCategory();
        ProductCategory category2 = exampleCategory();
        categoryDao.insert(category1);
        categoryDao.insert(category2);
        assertThat(categoryDao.list())
                .extracting(ProductCategory::getName)
                .contains(category1.getName(), category2.getName());
    }

    @Test
    void shouldRetrieveAllCategoryProperties() throws SQLException {
        categoryDao.insert(exampleCategory());
        categoryDao.insert(exampleCategory());
        ProductCategory category = exampleCategory();
        categoryDao.insert(category);
        assertThat(category).hasNoNullFieldsOrProperties();

        assertThat(categoryDao.retrieve(category.getId()))
                .usingRecursiveComparison()
                .isEqualTo(category);
    }

    private ProductCategory exampleCategory() {
        ProductCategory category = new ProductCategory();
        category.setName(exampleCategoryName());
        return category;
    }

    private String exampleCategoryName() {
        String[] options = {"Fruit", "Candy", "Non-food", "Dairy"};
        return options[random.nextInt(options.length)];
    }

}
