# Chequeamos que nos llegue el profile
if (( $# != 1 ))
then
  echo "Debe indicar el perfil que se esta construyendo (local, desa, qa, uat, prep, prod, test)"
  exit 1
fi

if [ ! -d ./deploy ]; then
    mkdir deploy
fi

rm ./deploy/*
cd ..

mvn clean package install -DskipTests

for D in `ls -d *{-service,pys-backoffice}`; do
    cp ./"$D"/target/*.jar ./assembler/deploy
    cp ./"$D"/recursos/*.service ./assembler/deploy
    cp ./"$D"/recursos/*.sh ./assembler/deploy
done

# Reemplazamos el perfil que estamos construyendo en los archivos de especificacion de servicio
sed -i "s/__PROFILE__/$1/g" ./assembler/deploy/*.service
