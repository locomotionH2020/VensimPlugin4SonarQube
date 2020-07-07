fs = require('fs');


module.exports = [
{
  path: '/integrationTest/qaGetSymbolsDefinition',
  method: 'POST',
  template: {symbols:
    [{name:"defined_variable",unit:"kg", definition:"A variable", "programming symbol type":"Variable", indexes:[],modules:{main:"module 1", secondary:[]},category:"some category"},
    {name:"DIFFERENT_COMMENT",unit:"kg", definition:"Not the expected comment", "programming symbol type":"Constant", indexes:[],modules:{main:"module 2", secondary:[]},category:"some category"},
    {name:"SAME_COMMENT",unit:"kg", definition:"    Expected comment    ", "programming symbol type":"Constant", indexes:[],modules:{main:"module 2", secondary:[]},category:"some category"},
    {name:"DIFFERENT_UNITS",unit:"Not the expected units", definition:"", "programming symbol type":"Constant", indexes:[],modules:{main:"module 2", secondary:[]},category:"some category"},
    {name:"SAME_UNITS",unit:"units", definition:"", "programming symbol type":"Constant", indexes:[],modules:{main:"module 2", secondary:[]},category:"some category"},
    {name:"type_mismatch",unit:"units", definition:"", "programming symbol type":"Reality_Check", indexes:[],modules:{main:"module 2", secondary:[]},category:"some category"},
    {name:"expected_type",unit:"units", definition:"", "programming symbol type":"Variable", indexes:[],modules:{main:"module 2", secondary:[]},category:"some category"},
    {name:"INDEX_MISMATCH",unit:"units",definition:"","programming symbol type":"Constant", indexes:["miRango","SUBSCRIPT"],modules:{main:"module 1", secondary:[]}, category:"foo"}
  ],
  indexes: [{name: "VALUE_MISMATCH", definition: "Subscript for the test 'testDictionarySubscriptValueMismatch.mdl'", values:["value1","value2"]},
    {name: "miRango", definition: "Subscript for the test 'testDictionaryIndexMismatch.mdl'", values:["VALOR_1","VALOR_2","VALOR_3","VALOR_4"]},
    {name: "SUBSCRIPT", definition: "Subscript for the test 'testDictionaryIndexMismatch.mdl'", values:["a","b","c"]}],
  modules:{main: "module 1", secondary:["module 2"]},
  categories: ["some category","another category","extremely creative category name"]

}

},
{path: '/integrationTest/qaAddSymbolsDefinition',
method: 'POST',
template: function(params,query,body){
  let expectedObject = {
    symbols: [{
        name: 'CONSTANT_NOT_IN_DB',
        unit: 'units',
        definition: 'comment',
        is_indexed: false,
        category: '',
        'programming symbol type': 'Constant'
      },
      {
        name: 'variable_not_in_db',
        unit: 'units',
        definition: 'comment',
        is_indexed: false,
        category: '',
        'programming symbol type': 'Variable'
      }
    ],
    indexes: [{
      name: 'SUBSCRIPT_NOT_IN_DB_I',
      values: ["VALUE_1", "VALUE_2"]
    }],
    module: 'testInjectSymbols.mdl'
  }
  
  console.log("Recibido: -----------------------------------------------------------");
  console.log(body)
  
  if(JSON.stringify(body)==JSON.stringify(expectedObject)){
    fs.writeFile("flagInjection", "it worked :D", function (err) {
      if (err) return console.log(err);
    });
  }
}
}
,
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
	"programming symbol type": "Constant"
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
	"programming symbol type": "Function"
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


