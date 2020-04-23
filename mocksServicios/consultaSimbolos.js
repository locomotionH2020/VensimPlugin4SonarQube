module.exports = [
{
  path: '/integrationTest/qaGetSymbolsDefinition',
  method: 'POST',
  template: {symbols:
    [{name:"defined_variable",unit:"kg", definition:"A variable", "programming symbol type":"Variable", indexes:[],modules:["module 1"],category:"some category"},
    {name:"DIFFERENT_COMMENT",unit:"kg", definition:"Not the expected comment", "programming symbol type":"Constant", indexes:[],modules:["module 2"],category:"some category"},
    {name:"SAME_COMMENT",unit:"kg", definition:"    Expected comment    ", "programming symbol type":"Constant", indexes:[],modules:["module 2"],category:"some category"},
    {name:"DIFFERENT_UNITS",unit:"Not the expected units", definition:"", "programming symbol type":"Constant", indexes:[],modules:["module 2"],category:"some category"},
    {name:"SAME_UNITS",unit:"units", definition:"", "programming symbol type":"Constant", indexes:[],modules:["module 2"],category:"some category"},
    {name:"type_mismatch",unit:"units", definition:"", "programming symbol type":"Reality_Check", indexes:[],modules:["module 2"],category:"some category"},
    {name:"expected_type",unit:"units", definition:"", "programming symbol type":"Variable", indexes:[],modules:["module 2"],category:"some category"},
    {name:"INDEX_MISMATCH",unit:"units",definition:"","programming symbol type":"Constant", indexes:["miRango","SUBSCRIPT"],modules:["module 1"], category:"foo"}
  ],
  indexes: [{name: "VALUE_MISMATCH", definition: "Subscript for the test 'testDictionarySubscriptValueMismatch.mdl'", values:["value1","value2"]},
    {name: "miRango", definition: "Subscript for the test 'testDictionaryIndexMismatch.mdl'", values:["VALOR_1","VALOR_2","VALOR_3","VALOR_4"]},
    {name: "SUBSCRIPT", definition: "Subscript for the test 'testDictionaryIndexMismatch.mdl'", values:["a","b","c"]}],
  modules:["module 1","module 2"],
  categories: ["some category","another category","extremely creative category name"]

}

}

]
