$ ->
  $.get "/discountDetails", (rows) ->
    $.each rows, (index, row) ->
      discountReport = $("<td>").text row.discountReport
      productorId = $("<td>").text row.productorId
      amount = $("<td>").text row.amount
      links = $("<td>").html '<a href="/requestRow_cancel/' + row.id + '" class="btn btn-danger">Cancelar</a>' + '<a href="/requestRow_fill/' + row.id + '" class="btn btn-primary">Entregar</a>' + '<a href="/requestRow_update/' + row.id + '" class="btn btn-primary">Editar</a>' + '<a href="/requestRow_remove/' + row.id + '" class="btn btn-danger">Eliminar</a>' + '<a href="/requestRow_show/' + row.id + '" class="btn btn-info">Mostrar</a>'
      $("#rows").append $("<tr>").append(discountReport).append(productorId).append(amount).append(links)
