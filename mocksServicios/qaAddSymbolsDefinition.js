fs = require('fs');


module.exports = [

{path: '/integrationTest/qaAddSymbolsDefinition',
method: 'POST',
template: function(params,query,body){
  let expectedObject = {
    symbols: [{
        name: 'CONSTANT_NOT_IN_DB',
        unit: 'units',
        definition: 'comment',
        isIndexed: "false",
        category: '',
        'ProgrammingSymbolType': 'Constant'
      },
      {
        name: 'variable_not_in_db',
        unit: 'units',
        definition: 'comment',
        isIndexed: "false",
        category: '',
        'ProgrammingSymbolType': 'Variable'
      }
    ],
    indexes: [{
      indexName: 'SUBSCRIPT_NOT_IN_DB_I',
      values: ["VALUE_1", "VALUE_2"]
    }],
    module: 'testInjectSymbols'
  }
  
  
  if(JSON.stringify(body)==JSON.stringify(expectedObject)){
    fs.writeFile("flagInjection", "it worked :D", function (err) {
      if (err) return console.log(err);
    });
  }
}
}

]


