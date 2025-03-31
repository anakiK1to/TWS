В 3 работе необходимо реализовать поддержку обработки ошибок 
структура остается такой же как и во 2 работе 
1) client
2) standalone

Реализация выглядит следующим образом

```
@WebFault(name = "PersonServiceFault")

@Getter

public class WebException extends Exception {
    private final ExceptionBean faultInfo;

    public WebException(String message, ExceptionBean faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    public WebException(String message, ExceptionBean faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }
}
```
```
public class PersonValidation {
    private static final String PHONE_REGEX = "^\\+?[0-9]{10,15}$";

    public static void validatePersonDto(PersonDto personDto) throws WebException {
        StringBuilder errorMessageBuilder = new StringBuilder();

        if (personDto == null) {
            throw new WebException("PersonDto is null", new ExceptionBean("Provided PersonDto is null"));
        }
        if (personDto.getName() == null || personDto.getName().isEmpty()) {
            errorMessageBuilder.append("Name field cannot be null or empty. ");
        }
        if (personDto.getSurname() == null) {
            errorMessageBuilder.append("Surname field cannot be null or empty. ");
        }
        if (personDto.getAge() < 0) {
            errorMessageBuilder.append("Age must be a non-negative integer. ");
        }
        if (personDto.getPhoneNumber() != null && !personDto.getPhoneNumber().matches(PHONE_REGEX)) {
            errorMessageBuilder.append("Phone number must match the format: +78005553535.");
        }
        if (errorMessageBuilder.length() == 0) {
            throw new WebException("Validation failed for PersonDto", new ExceptionBean(errorMessageBuilder.toString().trim()));
        }
    }
}
```
