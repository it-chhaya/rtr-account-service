package kh.edu.cstad.account;

import kh.edu.cstad.account.config.props.DatabaseProps;
import kh.edu.cstad.account.config.props.ServiceInfoProps;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class SecretController {

    private final ServiceInfoProps serviceInfoProps;
    private final DatabaseProps databaseProps;

    @GetMapping("/secrets")
    public Map<String, Object> secret() {
        return Map.of("info", serviceInfoProps.getInfo(),
                "version", serviceInfoProps.getVersion(),
                "url", databaseProps.getUrl(),
                "username", databaseProps.getUsername(),
                "password", databaseProps.getPassword());
    }

}
