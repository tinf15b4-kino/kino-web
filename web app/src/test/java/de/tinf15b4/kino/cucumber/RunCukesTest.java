package de.tinf15b4.kino.cucumber;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(glue = { "de.tinf15b4.kino.cucumber", "cucumber.api.spring" })
public class RunCukesTest {
}