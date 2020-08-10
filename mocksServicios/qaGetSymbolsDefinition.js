module.exports = [
{
path: '/integrationTest/qaGetSymbolsDefinition',
method: 'POST',
template: {symbols:
    [{name:"defined_variable",unit:"kg", definition:"A variable", "programmingSymbolType":"Variable", indexes:[],modules:{main:"module 1", secondary:[]},category:"some category"},
    {name:"DIFFERENT_COMMENT",unit:"kg", definition:"Not the expected comment", "programmingSymbolType":"Constant", indexes:[],modules:{main:"module 2", secondary:[]},category:"some category"},
    {name:"SAME_COMMENT",unit:"kg", definition:"    Expected comment    ", "programmingSymbolType":"Constant", indexes:[],modules:{main:"module 2", secondary:[]},category:"some category"},
    {name:"DIFFERENT_UNITS",unit:"Not the expected units", definition:"", "programmingSymbolType":"Constant", indexes:[],modules:{main:"module 2", secondary:[]},category:"some category"},
    {name:"SAME_UNITS",unit:"units", definition:"", "programmingSymbolType":"Constant", indexes:[],modules:{main:"module 2", secondary:[]},category:"some category"},
    {name:"type_mismatch",unit:"units", definition:"", "programmingSymbolType":"Reality_Check", indexes:[],modules:{main:"module 2", secondary:[]},category:"some category"},
    {name:"expected_type",unit:"units", definition:"", "programmingSymbolType":"Variable", indexes:[],modules:{main:"module 2", secondary:[]},category:"some category"},
    {name:"INDEX_MISMATCH",unit:"units",definition:"","programmingSymbolType":"Constant", indexes:["miRango","SUBSCRIPT"],modules:{main:"module 1", secondary:[]}, category:"foo"}
],
indexes: [
    {indexName: "VALUE_MISMATCH", definition: "Subscript for the test 'testDictionarySubscriptValueMismatch.mdl'", values:["value1","value2"]},
    {indexName: "miRango", definition: "Subscript for the test 'testDictionaryIndexMismatch.mdl'", values:["VALOR_1","VALOR_2","VALOR_3","VALOR_4"]},
    {indexName: "SUBSCRIPT", definition: "Subscript for the test 'testDictionaryIndexMismatch.mdl'", values:["a","b","c"]}],
modules:{main: "module 1", secondary:["module 2"]},
categories: ["some category","another category","extremely creative category name"]

}

},
{
    path: '/mocksBackend/qaGetSymbolsDefinition',
  method: 'POST',
  template:  {"symbols": [{
      "name": "SymbolExample1",
      "definition": "definition example for symbol 1 example",
      "unit": "Kg",
      "indexes": [],
      "modules": {
          "main": "IAM Number One",
          "secondary": []
      },
      "category": "CategoryExampleTopLevel",
      "programmingSymbolType": "Constant"
  }, {
      "name": "SymbolExample2",
      "definition": "definition example for symbol 2 example",
      "unit": "N",
      "indexes": ["IndexExample1", "IndexExample2"],
      "modules": {
          "main": "IAM Number One",
          "secondary": ["Testing_modules"]
      },
      "category": "CategoryExampleBottomLevel",
      "programmingSymbolType": "Function"
  }],
  "modules": ["IAM Number One", "Testing_modules"],
  "indexes": [{
      "name": "IndexExample1",
      "definition": "definition example 1",
      "values": ["ValueExample1"]
  }, {
      "name": "IndexExample2",
      "definition": "definition example 2",
      "values": ["ValueExample2", "ValueExample3"]
  }],
  "categories": [{
      "name": "CategoryExampleTopLevel",
      "level": 1,
      "super_category": "null"
  }, {
      "name": "CategoryExampleTopLevel",
      "level": 1,
      "super_category": "null"
  }]
  }
  }
]
