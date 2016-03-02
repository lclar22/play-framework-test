$ ->
  $.get "/cuentas/1", (bancos) ->
    $.each bancos, (index, banco) ->
      $("#name").html banco.nombre
      $("#type").html banco.tipo
