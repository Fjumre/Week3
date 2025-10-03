package app.dto;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@NoArgsConstructor
@Setter
@ToString
public class ProductDTO {

    private String name;
    private String mfr;
    private String type;
    private int calories;
    private int protein;
    private int fat;
    private int sodium;
    private float fiber;
    private float carbo;
    private int sugars;
    private int potass;
    private int vitamins;
    private int shelf;
    private float weight;
    private float cups;
    private float rating;



}
