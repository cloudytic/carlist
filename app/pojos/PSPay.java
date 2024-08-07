package pojos;

import converters.PSAuthConverter;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Convert;
import jakarta.persistence.Lob;
import javax.validation.constraints.Size;

public class PSPay {

    @Getter @Setter
    public String payFor;

    @Getter @Setter
    public String status;

    @Getter @Setter
    @Size(max=4000)
    @Convert(converter= PSAuthConverter.class)
    public PSAuth authorization;

    @Getter @Setter
    public Double amount;

    @Getter @Setter
    public String currency;

    @Getter @Setter
    @Column(length=100000)
    @Lob
    public String jsonContent;
}
