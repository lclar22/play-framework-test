row_id = location.pathname.split('/')[2]

$ ->
  $.get "/productRequestsByStorekeeper/" + row_id, (rows) ->
    $.each rows, (index, row) ->
      date = $("<td>").text row.date
      veterinario = $("<td>").text row.veterinario
      storekeeper = $("<td>").text row.storekeeper
      status = $("<td>").text row.status
      detail = $("<td>").text row.detail
      links = $("<td>").html '<a href="/productRequest_send/' + row.id + '">Enviar</a>' + '<a href="/productRequest_accept/' + row.id + '">Aceptar</a>' + '<a href="/productRequest_finish/' + row.id + '"><span class="glyphicon glyphicon-pencil">Finalizar</span></a>' + '<a href="/productRequest_update/' + row.id + '">Editar</a>' + '<a href="/productRequest_remove/' + row.id + '">Eliminar</a>' + '<a href="/productRequest_show/' + row.id + '">Mostrar</a>'
      $("#rows").append $("<tr>").append(date).append(veterinario).append(storekeeper).append(status).append(detail).append(links)
