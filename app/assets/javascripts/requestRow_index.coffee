$ ->
  $.get "/requestRows", (rows) ->
    $.each rows, (index, row) ->
      requestId = $("<td>").text row.requestId
      productId = $("<td>").text row.productId
      productorId = $("<td>").text row.productorId
      quantity = $("<td>").text row.quantity
      status = $("<td>").text row.status
      links = $("<td>").html '<a href="/requestRow_update/' + row.id + '"><span class="glyphicon glyphicon-pencil">Editar</span></a>' + '<a href="/requestRow_remove/' + row.id + '"><span class="glyphicon glyphicon-remove">Eliminar</span></a>' + '<a href="/requestRow_show/' + row.id + '"><span class="glyphicon glyphicon-remove">Mostrar</span></a>'
      $("#rows").append $("<tr>").append(requestId).append(productId).append(productorId).append(quantity).append(status).append(links)
