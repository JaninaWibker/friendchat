# Just a quick script to reset the database as this needs to happen quite
# frequently atm. This "works on my machine" and will be replaced when
# proper database creation is implemented
dropdb friendchat
createdb friendchat
psql -U janina -d friendchat -c "create extension \"uuid-ossp\";"
psql -U friendchat -d friendchat -f ./db.sql
