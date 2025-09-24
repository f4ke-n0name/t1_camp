package com.f4ken0name.github.command.models;

import com.f4ken0name.github.command.utils.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Command {

    @Size(max = 1000)
    @NotBlank
    private String description;
    @NotNull
    private Priority priority;
    @Size(max=100)
    @NotBlank
    private String author;
    @NotNull
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}(T\\d{2}:\\d{2}:\\d{2}(\\.\\d+)?(Z|[+-]\\d{2}:\\d{2})?)?$")
    private String time;

}