<!-- @format -->

# E-commerce Project

A simple e-commerce application built with Java and Spring Boot.

## Technologies Used

- Java
- Spring Boot
- Maven

## Setup and Installation

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/Falasefemi2/ecommerce.git
    ```
2.  **Navigate to the project directory:**
    ```bash
    cd ecommerce
    ```
3.  **Build the project:**
    ```bash
    ./mvnw clean install
    ```

## Usage

Run the application using the following command:

```bash
./mvnw spring-boot:run
```

The application will be available at `http://localhost:3080`.

## API Endpoints

The following endpoints are available:

### Product Endpoints

- `GET /api/v1/products`: Get a list of all products.
- `GET /api/v1/products/{productId}`: Get a specific product by its ID.
- `POST /api/v1/products`: Create a new product.
- `PUT /api/v1/products/{id}`: Update an existing product.
- `DELETE /api/v1/products/{id}`: Delete a product by its ID.
- `GET /api/v1/products/brand-and-name?brand={brand}&name={name}`: Find products by brand and name.
- `GET /api/v1/products/category-and-brand?category={category}&brand={brand}`: Find products by category and brand.
- `GET /api/v1/products/name?name={name}`: Find products by a name containing the given string.
- `GET /api/v1/products/brand?brand={brand}`: Find products by brand.
- `GET /api/v1/products/category/{category}`: Find products by category name.
- `GET /api/v1/products/count?brand={brand}&name={name}`: Count products by brand and name.

### Category Endpoints

- `GET /api/v1/categories`: Get a list of all categories.
- `POST /api/v1/categories/add`: Create a new category.
- `GET /api/v1/categories/{id}`: Get a specific category by its ID.
- `GET /api/v1/categories/by-name`: Find products by category and brand.
- `DELETE /api/v1/categories/{id}`: Delete a category by its ID.
- `PUT /api/v1/categories/{id}`: Update an existing category.

### Image Endpoints

- `POST /api/v1/images`: Upload one or more images for a product.
- `GET /api/v1/images/download/{imageId}`: Download an image by its ID.
- `PUT /api/v1/images/{imageId}/update`: Update an existing image.
- `DELETE /api/v1/images/{imageId}`: Delete an image by its ID.

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.
