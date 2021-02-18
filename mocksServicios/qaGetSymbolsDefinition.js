module.exports = [
    {
        path: '/integrationTest/qaGetSymbolsDefinition',
        method: 'POST',
        template: {
            symbols:
                [{
                    name: "defined_variable",
                    unit: "kg",
                    definition: "A variable",
                    "programmingSymbolType": "Variable",
                    indexes: [],
                    modules: {main: "module 1", secondary: []},
                    category: "some category"
                },
                    {
                        name: "DIFFERENT_COMMENT",
                        unit: "kg",
                        definition: "Not the expected comment",
                        "programmingSymbolType": "Constant",
                        indexes: [],
                        modules: {main: "module 2", secondary: []},
                        category: "some category"
                    },
                    {
                        name: "SAME_COMMENT",
                        unit: "kg",
                        definition: "    Expected comment    ",
                        "programmingSymbolType": "Constant",
                        indexes: [],
                        modules: {main: "module 2", secondary: []},
                        category: "some category"
                    },
                    {
                        name: "DIFFERENT_UNITS",
                        unit: "Not the expected units",
                        definition: "",
                        "programmingSymbolType": "Constant",
                        indexes: [],
                        modules: {main: "module 2", secondary: []},
                        category: "some category"
                    },
                    {
                        name: "SAME_UNITS",
                        unit: "units",
                        definition: "",
                        "programmingSymbolType": "Constant",
                        indexes: [],
                        modules: {main: "module 2", secondary: []},
                        category: "some category"
                    },
                    {
                        name: "type_mismatch",
                        unit: "units",
                        definition: "",
                        "programmingSymbolType": "Reality_Check",
                        indexes: [],
                        modules: {main: "module 2", secondary: []},
                        category: "some category"
                    },
                    {
                        name: "expected_type",
                        unit: "units",
                        definition: "",
                        "programmingSymbolType": "Variable",
                        indexes: [],
                        modules: {main: "module 2", secondary: []},
                        category: "some category"
                    },
                    {
                        name: "INDEX_MISMATCH",
                        unit: "units",
                        definition: "",
                        "programmingSymbolType": "Constant",
                        indexes: ["miRango", "SUBSCRIPT"],
                        modules: {main: "module 1", secondary: []},
                        category: "foo"
                    }
                ],
            indexes: [
                {
                    indexName: "VALUE_MISMATCH",
                    definition: "Subscript for the test 'testDictionarySubscriptValueMismatch.mdl'",
                    values: ["value1", "value2"]
                },
                {
                    indexName: "miRango",
                    definition: "Subscript for the test 'testDictionaryIndexMismatch.mdl'",
                    values: ["VALOR_1", "VALOR_2", "VALOR_3", "VALOR_4"]
                },
                {
                    indexName: "SUBSCRIPT",
                    definition: "Subscript for the test 'testDictionaryIndexMismatch.mdl'",
                    values: ["a", "b", "c"]
                }],
            modules: {main: "module 1", secondary: ["module 2"]},
            categories: ["some category", "another category", "extremely creative category name"]

        }

    },
    {
        path: '/qaGetSymbolsDefinition',
        method: 'POST',
        template: {
            "symbols": [{
                "name": "total_jobs_biofuels",
                "definition": "Total (direct+indirect) jobs biofuels.",
                "unit": "people",
                "indexes": [],
                "modules": {
                    "main": "economy",
                    "secondary": []
                },
                "category": "RES_employment",
                "programmingSymbolType": "Variable"
            }],
            "indexes": []
        }
    }
]
