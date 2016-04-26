row_id = location.pathname.split('/')[2]

$ ->
  $.get "/transactionDetails/" + row_id, (rows) ->
    $.each rows, (index, row) ->
      transactionId = $("<td>").text row.transactionId
      accountId = $("<td>").text row.accountId
      amount = $("<td>").text row.amount
      links = $("<td>").html '<a href="/transactionDetail_update/' + row.id + '" class="btn btn-primary">Editar</a>' + '<a href="/transactionDetail_remove/' + row.id + '" class="btn btn-danger">Eliminar</a>' + '<a href="/transactionDetail_show/' + row.id + '" class="btn btn-info">Mostrar</a>'
      $("#rows").append $("<tr>").append(transactionId).append(accountId).append(amount).append(links)
