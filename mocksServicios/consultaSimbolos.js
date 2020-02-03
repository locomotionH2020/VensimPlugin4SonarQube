module.exports = [{
  path: '/symbols/all',
  method: 'POST',
  template: (params, query, body) => {
    console.log(body);
    symbols = [];
    for (let i = 0; i < body.length; i++) {
      symbols.push({ "symbol": body[i] });
    }
    console.log(symbols);
    return symbols;
  }

},
{
  path: '/symbols/integrationTest',
  method: 'POST',
  template: [{symbol:"PRODUCTIVITY"}]

}

]
