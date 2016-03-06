account_value = location.pathname.split('/')[2]

$ ->
  $.get "/cuentas/" + account_value, (bancos) ->
    $.each bancos, (index, banco) ->
      $("#name").html banco.nombre
      $("#type").html banco.tipo
