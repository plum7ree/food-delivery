import json
import psycopg2
from psycopg2.extras import execute_values
import json_stream

# Define database connection parameters
DB_HOST = 'localhost'
DB_NAME = 'db'
DB_USER = 'admin'
DB_PASSWORD = '1234'
DB_PORT = '5432'

# Define the batch size
BATCH_SIZE = 1000

USER_FILE_PATH = 'user_list.json'
RESTAURANT_FILE_PATH = 'web_crawled_restaurant_data.json'


def create_tables_if_not_exist(conn, cursor):
    """
    Check if the required tables exist, and create them if they don't.
    """
    # Execute the SQL script to create the tables
    with open('init-schema.sql', 'r') as f:
        sql_script = f.read()
        cursor.execute(sql_script)
        conn.commit()
    cursor.execute("""
        SELECT table_name
        FROM information_schema.tables
        WHERE table_schema = 'user_schema';
    """)
    tables = [row[0] for row in cursor.fetchall()]
    print(tables)


def get_json_data(file_path):
    """
    Generator to yield data from JSON file in chunks.
    """
    with open(file_path, 'r') as file:
        while True:
            lines = file.readlines(BATCH_SIZE)
            if not lines:
                break
            for line in lines:
                yield json.loads(line)


def insert_batch(cursor, data_batch):
    """
    Insert a batch of data into the database.
    """
    # Adjust the insert statement according to your table structure
    restaurant_insert_query = """
    SET search_path TO user_schema;
    INSERT INTO restaurant (id, account_id, name, type, open_time, close_time, picture_url1)
    VALUES %s
    """

    '''
    INSERT
    INTO
    menu(restaurant_id, name, description, price, picture_url)
    VALUES %s
    '''

    '''
    INSERT INTO option_group (restaurant_id, description, max_select_number, necessary)
    VALUES %s
    '''

    '''
    INSERT INTO option (option_id, name, cost)
    VALUES %s
    '''

    # Convert the list of dictionaries to a list of tuples
    values = [restaurant for restaurant in data_batch]
    execute_values(cursor, restaurant_insert_query, values)


def migrate_user(conn, cursor, file_path):
    def insert_batch(cursor, data_batch):
        """
        Insert a batch of data into the database.
        """
        # Adjust the insert statement according to your table structure
        restaurant_insert_query = """
        SET search_path TO user_schema;
        INSERT INTO account (id, username, password, email, role)
        VALUES %s
        """


        # Convert the list of dictionaries to a list of tuples
        values = [restaurant for restaurant in data_batch]
        execute_values(cursor, restaurant_insert_query, values)
    data_batch = []
    with open(file_path, 'r') as f:
        data = json_stream.load(f)  # data is a transient dict-like object

        for user in data:
            # 주의. TRANSIENT 모드에서는 json 에 있는 필드 순서대로 접근해야함. 뒤에것 먼저 접근하면 이전것 접근 불가. streaming 이여서.
            # PERSISTENT 모드로 하면 가능하긴 할듯?
            data_batch.append((user['id'], user['username'], user['password'],
                               user['email'], user['role']))
            if len(data_batch) == BATCH_SIZE:
                insert_batch(cursor, data_batch)
                conn.commit()
                data_batch.clear()

        # Insert any remaining data
        if data_batch:
            insert_batch(cursor, data_batch)
            conn.commit()

def migrate_restaurant(conn, cursor, file_path):
    def insert_batch(cursor, data_batch):
        """
        Insert a batch of data into the database.
        """
        # Adjust the insert statement according to your table structure
        restaurant_insert_query = """
        SET search_path TO user_schema;
        INSERT INTO restaurant (id, account_id, name, type, open_time, close_time, picture_url1)
        VALUES %s
        """

        # Convert the list of dictionaries to a list of tuples
        values = [restaurant for restaurant in data_batch]
        execute_values(cursor, restaurant_insert_query, values)

    # Read data from JSON file and insert into database in batches
    data_batch = []
    with open(file_path, 'r') as f:
        data = json_stream.load(f)  # data is a transient dict-like object

        for restaurant in data:
            # 주의. TRANSIENT 모드에서는 json 에 있는 필드 순서대로 접근해야함. 뒤에것 먼저 접근하면 이전것 접근 불가. streaming 이여서.
            # PERSISTENT 모드로 하면 가능하긴 할듯?
            data_batch.append((restaurant['id'], restaurant['userId'], restaurant['name'],
                               restaurant['type'], restaurant['openTime'], restaurant['closeTime'],
                               restaurant['pictureUrl1']))
            if len(data_batch) == BATCH_SIZE:
                insert_batch(cursor, data_batch)
                conn.commit()
                data_batch.clear()

        # Insert any remaining data
        if data_batch:
            insert_batch(cursor, data_batch)
            conn.commit()


def main():
    # Connect to the PostgreSQL database
    conn = psycopg2.connect(
        host=DB_HOST,
        database=DB_NAME,
        user=DB_USER,
        password=DB_PASSWORD,
        port=DB_PORT
    )
    print(f'connection status: {conn.status}')
    cursor = conn.cursor()

    create_tables_if_not_exist(conn, cursor)

    migrate_user(conn, cursor, USER_FILE_PATH)
    migrate_restaurant(conn, cursor, RESTAURANT_FILE_PATH)

    # Close the database connection
    cursor.close()
    conn.close()


if __name__ == "__main__":
    main()
