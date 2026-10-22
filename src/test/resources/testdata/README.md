# External test data

All fixture paths are classpath-relative to `testdata/`. `TestData.load` selects a unique `caseId` and maps it to the
requested record type. Postal codes remain strings.

| Files                                   | Record                 | Cases                                                                                         |
|-----------------------------------------|------------------------|-----------------------------------------------------------------------------------------------|
| `data.csv`, `data.xlsx`, `login.json`   | `FixtureData.Login`    | Five enabled accounts, locked account, wrong password, missing username, missing password     |
| `productItem.json`, `productItem.yml`   | `FixtureData.Product`  | All six products with stable ID, title, full slug and displayed price                         |
| `checkoutInfo.json`, `checkoutInfo.yml` | `FixtureData.Checkout` | Valid UK data, leading-zero postal code, punctuation, each missing field and all fields empty |

CSV and Excel contain the same login schema. The Excel reader uses the first worksheet, `LoginTest`, with headers in row

1. JSON and YAML contain lists of case objects. Quote YAML strings, especially empty strings and postal codes. Postal
   codes are text, never numbers.

The fixture reader supports CSV, XLSX, JSON and YAML resources on the test classpath. Run the project with Java 17 or
later:

```powershell
mvn clean verify -Dheadless.mode=true
```

The JUnit runner intentionally leaves tag selection overridable. `@CartPrice` is the default in
`cucumber.properties`; Maven, Compose, and Jenkins may replace it with `-Dcucumber.filter.tags` or
`CUCUMBER_FILTER_TAGS`. Fixture loading remains available through
`TestData.load(file, caseId, FixtureData.Login.class)` and corresponding product/checkout record types.
