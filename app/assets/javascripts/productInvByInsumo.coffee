row_id = location.pathname.split('/')[2]

$ ->
  $.get "/ProductInvsByInsumo/" + row_id, (rows) ->
    $.each rows, (index, row) ->
      productId = $("<td>").text row.productId
      proveedorId = $("<td>").text row.proveedorId
      amount = $("<td>").text row.amount
      amountLeft = $("<td>").text row.amountLeft
      links = $("<td>").html '<a href="/productInv_update/' + row.id + '"><span class="glyphicon glyphicon-pencil">Editar</span></a>' + '<a href="/productInv_remove/' + row.id + '"><span class="glyphicon glyphicon-remove">Eliminar</span></a>' + '<a href="/productInv_show/' + row.id + '"><span class="glyphicon glyphicon-remove">Mostrar</span></a>'
      $("#rows").append $("<tr>").append(productId).append(proveedorId).append(amount).append(amountLeft).append(links)
