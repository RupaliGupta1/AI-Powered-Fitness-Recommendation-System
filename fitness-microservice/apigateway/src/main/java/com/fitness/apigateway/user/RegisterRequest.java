package com.fitness.apigateway.user;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Email should not blank")
    @Email(message = "Invalid Email")
    private String email;
    public String keyCloakId;

    @NotBlank(message = "Password should not blank")
    @Size(min = 6,message = "Password must have atleast 6 characters")
    private String password;
    private String firstName;
    private String lastName;

    public @NotBlank(message = "Email should not blank") @Email(message = "Invalid Email") String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank(message = "Email should not blank") @Email(message = "Invalid Email") String email) {
        this.email = email;
    }

    public @NotBlank(message = "Password should not blank") @Size(min = 6, message = "Password must have atleast 6 characters") String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank(message = "Password should not blank") @Size(min = 6, message = "Password must have atleast 6 characters") String password) {
        this.password = password;
    }

}
