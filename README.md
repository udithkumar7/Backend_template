# PDF Generator Template

A Spring Boot template for PDF generation using iText 8. This template provides a flexible and reusable solution for generating PDF documents in your Spring Boot applications.

## Features

- PDF generation with custom templates
- REST API endpoints for PDF generation
- Configurable PDF output
- Error handling and logging
- Sample implementation

## Prerequisites

- Java 17
- Maven
- Spring Boot 3.2.3

## Getting Started

1. Clone the template
2. Import the project into your IDE
3. Run `mvn clean install` to build the project
4. Run the application using `mvn spring-boot:run`

## Usage

### Basic PDF Generation

```http
POST /api/pdf/generate
Content-Type: application/json

{
    "title": "Sample Document",
    "content": "This is a sample PDF content",
    "author": "John Doe",
    "date": "2024-03-20"
}
```

### PDF Generation with Template

```http
POST /api/pdf/generate/{template}
Content-Type: application/json

{
    "title": "Sample Document",
    "content": "This is a sample PDF content",
    "author": "John Doe",
    "date": "2024-03-20"
}
```

## Customization

1. Create your own DTOs by extending or modifying `SamplePdfData`
2. Implement custom templates in the `templates/pdf` directory
3. Modify `PdfGeneratorServiceImpl` to add your custom PDF generation logic

## Configuration

The following properties can be configured in `application.properties`:

- `server.port`: Server port (default: 8080)
- `pdf.template.directory`: Directory for PDF templates
- `pdf.output.directory`: Directory for generated PDFs

## Error Handling

The template includes basic error handling and logging. All PDF generation errors are logged and wrapped in a `RuntimeException`.

## Contributing

Feel free to submit issues and enhancement requests. 