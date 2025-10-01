package app.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.experimental.categories.Category;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Product {

    @Id
    @Column(name= "pId", nullable= false, unique= true)
    private String pId;

    @Column(name= "name", nullable= false)
    private String name;

    @Column(name= "mfr", nullable= false)
    private Category mfr;

    @Column(name= "type", nullable= false)
    private Category type;

    @Column(name= "calories", nullable= false)
    private int calories;

    @Column(name= "protein", nullable= false)
    private int protein;

    @Column(name= "fat", nullable= false)
    private int fat;

    @Column(name= "sodium", nullable= false)
    private int sodium;

    @Column(name= "fiber", nullable= false)
    private float fiber;

    @Column(name= "carbo", nullable= false)
    private float carbo;

    @Column(name= "sugars", nullable= false)
    private int sugars;

    @Column(name= "potass", nullable= false)
    private int potass;

    @Column(name= "vitamins", nullable= false)
    private int vitamins;

    @Column(name= "shelf", nullable= false)
    private int shelf;

    @Column(name= "weight", nullable= false)
    private float weight;

    @Column(name= "cups", nullable= false)
    private float cups;

    @Column(name= "rating", nullable= false)
    private float rating;

}