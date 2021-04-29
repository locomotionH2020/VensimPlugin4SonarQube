package es.uva.locomotion.utilities;

import es.uva.locomotion.model.DataBaseRepresentation;
import es.uva.locomotion.model.Module;
import es.uva.locomotion.model.ViewTable;
import es.uva.locomotion.model.category.Category;
import es.uva.locomotion.model.category.CategoryMap;
import es.uva.locomotion.model.symbol.Symbol;
import es.uva.locomotion.model.symbol.SymbolTable;
import es.uva.locomotion.model.symbol.SymbolType;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;

import java.util.Set;

import static org.junit.Assert.*;

public class JsonDictoinaryDiffBuilderTest {

    @Test
    public void Emptytest() {
        JsonDictoinaryDiffBuilder diff = new JsonDictoinaryDiffBuilder();
        assertEquals(Json.createArrayBuilder().build(), diff.build());
    }

    @Test
    public void testOnlyFilename() {
        JsonDictoinaryDiffBuilder diff = new JsonDictoinaryDiffBuilder();
        diff.addTables("filename", new SymbolTable(), new ViewTable(), new DataBaseRepresentation());
        JsonArrayBuilder expected = Json.createArrayBuilder();
        expected.add(Json.createObjectBuilder().add("file", "filename"));
    }

    @Test
    public void testAll() {
        JsonDictoinaryDiffBuilder diff = new JsonDictoinaryDiffBuilder();

        SymbolTable symbolTable = new SymbolTable();
        SymbolTable dbSymbolTable = new SymbolTable();
        Symbol sSame = new Symbol("symbolName");

        Symbol sDiff = new Symbol("symbolNameDiff");
        sDiff.setCategory(Category.create("sameCategory"));
        sDiff.setType(SymbolType.VARIABLE);
        sDiff.setComment("Comment");
        sDiff.setDependencies(Set.of());
        sDiff.setGroup("Group");
        sDiff.setPrimaryModule(new Module("primary"));
        sDiff.addShadowModule(new Module("shadow"));
        Symbol sDiff2 = new Symbol("symbolNameDiff");
        sDiff2.setCategory(Category.create("sameCategoryDiff"));
        sDiff2.setComment("CommentDiff");
        sDiff2.setDependencies(Set.of(new Symbol("Diff")));
        sDiff2.setGroup("GroupDiff");
        sDiff2.setPrimaryModule(new Module("primaryDiff"));
        sDiff2.addShadowModule(new Module("shadowDiff"));

        Symbol sDB = new Symbol("symbolNameDB");
        Symbol sLocal = new Symbol("symbolNameLocal");
        sLocal.setType(SymbolType.SUBSCRIPT_VALUE);

        symbolTable.addSymbol(sSame);
        dbSymbolTable.addSymbol(sSame);

        symbolTable.addSymbol(sDiff);
        dbSymbolTable.addSymbol(sDiff2);

        symbolTable.addSymbol(sLocal);
        dbSymbolTable.addSymbol(sDB);

        ViewTable viewTable = new ViewTable();
        viewTable.createOrSelectView("moduleSame", "categorySame", "subcategorySame");
        viewTable.createOrSelectView("moduleSame", "categoryAnother", "subcategorySame");
        viewTable.createOrSelectView("moduleSame", "categorySame", "subcategorySameLocal");
        viewTable.createOrSelectView("moduleLocal", "categoryLocal", "subcategoryLocal");
        DataBaseRepresentation dataBaseRepresentation = new DataBaseRepresentation();
        dataBaseRepresentation.setDataBaseSymbols(dbSymbolTable);
        CategoryMap categoryMap = new CategoryMap();
        categoryMap.createOrSelectCategory("categorySame");
        categoryMap.addSubcategoryTo("categorySame", "subcategorySame");
        categoryMap.createOrSelectCategory("categoryAnother");
        categoryMap.addSubcategoryTo("categoryAnother", "subcategorySame");
        categoryMap.addSubcategoryTo("categorySame", "subcategorySameDB");
        categoryMap.createOrSelectCategory("categoryDB");
        categoryMap.addSubcategoryTo("categoryDB", "subcategoryDB");
        dataBaseRepresentation.setCategories(categoryMap);

        dataBaseRepresentation.setModules(Set.of(new Module("moduleSame"), new Module("moduleDB")));
        diff.addTables("filename", symbolTable, viewTable, dataBaseRepresentation);
        String expected = "[\n" +
                "  {\n" +
                "    \"file\": \"filename\",\n" +
                "    \"symbols\": {\n" +
                "      \"missmatches\": {\n" +
                "        \"symbolNameDiff\": {\n" +
                "          \"primary\": {\n" +
                "            \"Local\": \"primary\",\n" +
                "            \"Dictionary\": \"primaryDiff\"\n" +
                "          },\n" +
                "          \"shadows\": {\n" +
                "            \"Local\": [\n" +
                "              \"shadow\"\n" +
                "            ],\n" +
                "            \"Dictionary\": [\n" +
                "              \"shadow\"\n" +
                "            ]\n" +
                "          },\n" +
                "          \"category\": {\n" +
                "            \"Local\": \"sameCategory\",\n" +
                "            \"Dictionary\": \"sameCategoryDiff\"\n" +
                "          },\n" +
                "          \"comment\": {\n" +
                "            \"Local\": \"Comment\",\n" +
                "            \"Dictionary\": \"CommentDiff\"\n" +
                "          }\n" +
                "        }\n" +
                "      },\n" +
                "      \"not_found_in_DB\": [],\n" +
                "      \"not_found_in_local\": [\n" +
                "        \"symbolNameDB\"\n" +
                "      ]\n" +
                "    },\n" +
                "    \"indexes\": {\n" +
                "      \"missmatches\": {},\n" +
                "      \"not_found_in_DB\": [],\n" +
                "      \"not_found_in_local\": [\n" +
                "      ]\n" +
                "    },\n" +
                "    \"indexes_values\": {\n" +
                "      \"missmatches\": {},\n" +
                "      \"not_found_in_DB\": [\n" +
                "        \"symbolNameLocal\"\n" +
                "      ],\n" +
                "      \"not_found_in_local\": [\n" +
                "      ]\n" +
                "    },\n" +
                "    \"modules\": {\n" +
                "      \"not_found_in_DB\": [\n" +
                "        \"moduleLocal\"\n" +
                "      ],\n" +
                "      \"not_found_in_local\": [\n" +
                "        \"moduleDB\"\n" +
                "      ]\n" +
                "    },\n" +
                "    \"categories\": {\n" +
                "      \"missmatches\": {},\n" +
                "      \"not_found_in_DB\": [\n" +
                "        \"categoryLocal\",\n" +
                "        \"categoryLocal.subcategoryLocal\",\n" +
                "        {\n" +
                "          \"super\": {\n" +
                "            \"Local\": \"categoryAnother\",\n" +
                "            \"Dictionary\": \"categorySame\"\n" +
                "          }\n" +
                "        },\n" +
                "        \"categorySame.subcategorySameLocal\"\n" +
                "      ],\n" +
                "      \"not_found_in_local\": [\n" +
                "        \"categoryDB\",\n" +
                "        \"categoryDB.subcategoryDB\",\n" +
                "        \"categorySame.subcategorySameDB\"\n" +
                "      ]\n" +
                "    }\n" +
                "  }\n" +
                "]";
        assertEquals(expected.replaceAll("\\s+", ""), diff.build().toString());
    }

}